package me.wilkai.deathswap;

import me.wilkai.deathswap.config.Config;
import me.wilkai.deathswap.util.Debug;
import me.wilkai.deathswap.util.LinearTimer;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Random;

/**
 * The plugin which contains the actual Deathswap Game itself.
 * @see DeathswapPlugin For the main class of the Plugin.
 * @see Main For the class that is called when the Plugin is executed incorrectly.
 */
public class Deathswap {

    private final Random random; // RNG mostly used for Coordinating the next Swap Interval.
    private final DeathswapPlugin plugin; // The Instance of the Deathswap Plugin.
    private final ArrayList<Player> players; // The list of all Living Players.
    private Config config; // The Configuration that Dictates how the Game should Behave.
    private BukkitTask task; // Task that is executed every second, counts down to the end of the Grace Period and the occurrence of the Next Swap.
    private LinearTimer timer; // Timer at the top of the Players screen displaying time until next swap. (And time until Grace Period ends)
    private boolean started; // Has the Deathswap Started?
    private int gracePeriodRemaining; // How many seconds remain of the Grace Period.
    private int initialSwapTime; // The time between the last swap and the next swap.
    private int timeRemaining; // The Time Remaining until the Next Swap. (In Seconds)
    private boolean swapPlayersForward; // If true when Swapping, Player[n] will be teleported to Player[n + 1] otherwise they will be teleported to Player[n - 1]

    /**
     * Creates a New Instance of the Deathswap Class with the Plugin and Config provided.
     * @param plugin The Deathswap Plugin's Instance.
     */
    public Deathswap(DeathswapPlugin plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
        this.players = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * Starts the Deathswap!
     * <i>Does not verify that all conditions are good, this should be handled by the respective command.</i>
     */
    public void start() {
        Debug.log("Starting the Deathswap.");

        this.started = true;
        this.gracePeriodRemaining = config.gracePeriod;
        this.scheduleNextSwap();

        Bukkit.broadcastMessage(config.deathswapBegin);

        timer = new LinearTimer("Timer");
        timer.setColor(BarColor.WHITE);
        timer.setStyle(BarStyle.SOLID);

        // Make the timer visible to all Players.
        Bukkit.getOnlinePlayers().forEach(player -> {
            timer.addPlayer(player);
        });

        if(gracePeriodRemaining != 0) {
            int minutes = gracePeriodRemaining / 60;

            timer.setMax(config.gracePeriod);
            timer.setProgress(gracePeriodRemaining);
            timer.setColor(BarColor.GREEN);

            Bukkit.broadcastMessage(config.gracePeriodWarning.replace("<minutes>", String.valueOf(minutes)));
        }
        else { // No Grace Period - Change the Timer to display time till next Swap.
            timer.setColor(BarColor.RED);
            timer.setCountType(LinearTimer.CountType.COUNTUP);
            timer.setMax(initialSwapTime);
            timer.setProgress(0);
            timer.setTitle("Next Swap");
        }

        if(config.showTimer) {
            timer.setVisible(true);
        }

        this.task = new BukkitRunnable() {
            public void run() {
                if(gracePeriodRemaining > 0) {
                    gracePeriodRemaining--;

                    timer.setProgress(gracePeriodRemaining);

                    if(gracePeriodRemaining % 60 == 0) {
                        String message; // Message that is sent to everyone.
                        int minutesLeft = gracePeriodRemaining / 60;

                        if(minutesLeft == 0) {
                            message = config.gracePeriodEnd;

                            timer.setColor(BarColor.RED);
                            timer.setCountType(LinearTimer.CountType.COUNTUP);
                            timer.setMax(initialSwapTime);
                            timer.setProgress(0);
                            timer.setTitle("Next Swap");
                        }
                        else if(minutesLeft == 1) {
                            message = config.gracePeriodFinalWarning;
                        }
                        else {
                            message = config.gracePeriodWarning.replace("<minutes>", String.valueOf(minutesLeft));
                        }

                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
                    }

                    if(gracePeriodRemaining <= 5 && gracePeriodRemaining != 0) {
                        String message = config.gracePeriodCountdown.replace("<seconds>", String.valueOf(gracePeriodRemaining));

                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
                    }

                    return;
                }

                timeRemaining--;
                timer.setProgress(timeRemaining);

                if(timeRemaining <= 10 && timeRemaining != 0) { // If it isn't time to swap yet but it will be soon.
                    String message = config.swappingSoon.replace("<seconds>", String.valueOf(timeRemaining));

                    players.forEach(player -> player.sendTitle("§c" + timeRemaining, "", 0, 25, 0));
                    players.forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f));

                    Bukkit.broadcastMessage(message);
                }
                else if(timeRemaining <= 0) { // If it's time to swap
                    String message = config.swappingNow;

                    players.forEach(player -> player.sendTitle("§aSwap!", "", 0, 20, 10));
                    players.forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f));

                    Bukkit.broadcastMessage(message);
                    scheduleNextSwap(); // Schedule the Next Swap before executing the Current One in order to avoid timing issues.
                    swap(); // Execute the swap.
                    timer.setMax(initialSwapTime);
                }

            }
        }.runTaskTimer(plugin, 0, 20);
    }

    /**
     * Stops the Deathswap as long as there are less than 2 people alive.
     * If there is 1 Player alive they are declared the Winner.
     * If there are no Players alive the Plugin will check which two Players died last and declare a tie between them.
     *
     * @see Deathswap#cancel() To cancel the Deathswap with no winners.
     */
    public void stop() {
        Debug.log("Attempting to Stop.");

        if(!this.started) {
            Debug.log("Tried to Stop but failed as Deathswap hasn't been Started.");
            return;
        }

        if(players.size() == 0) { // Tie!
            Debug.log("Stopped Successfully with a Draw.");

            String message = config.deathswapTieTitle;
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendTitle(message, config.deathswapTieSubtitle, 40, 100, 10);
            });

            this.started = false;
            this.task.cancel();
        }
        else if(players.size() > 1) { // Wrong Usage!
            // If there is still more than 1 Player then the Deathswap has not ended yet.
            Debug.log("Tried to Stop but failed as more than 1 Player lives.");
            return;
        }
        else {
            this.started = false;
            this.task.cancel(); // Prevent another Swap from occurring.

            Player winner = players.get(0); // Get the Winner.
            String message = config.deathswapDefeatTitle.replace("<winner>", winner.getDisplayName());

            // Loop through all online Players.
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(player != winner) { // If this Player lost.
                    player.teleport(winner.getLocation()); // Teleport the Player to the Winner's Location.
                    player.sendTitle(message, config.deathswapDefeatSubtitle, 60, 120, 30); // Send the Player a nice friendly message.
                }
                else { // If this Player won!
                    player.sendTitle(config.deathswapVictoryTitle, config.deathswapVictorySubtitle, 70, 130, 40);
                }
            });

            World world = winner.getWorld(); // Get the world that the Winner is in.
            Location location = winner.getLocation(); // Get the exact location of the Winner at the current time.

            // One firework for each dead enemy.
            for(int i = 0; i < Bukkit.getOnlinePlayers().size() - 1; i++) {
                // Offset the X and Z axis by -1 to 1 block.
                double x = location.getX() + random.nextInt(3) - 1;
                double z = location.getZ() + random.nextInt(3) - 1;

                Color color = Color.fromRGB((int)(random.nextDouble() * 0x1000000)); // Generate a Random Color.
                FireworkEffect.Type type = FireworkEffect.Type.values()[random.nextInt(5)]; // Pick a Random Firework Effect.

                // Generate a random Firework Effect.
                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(color)
                        .flicker(random.nextBoolean())
                        .trail(random.nextBoolean())
                        .with(type)
                        .build();

                // Determine the Location the Firework will be Spawned at.
                Location spawnLocation = new Location(world, x, location.getY(), z);

                // Spawn the Firework with the given effects.
                Firework firework = world.spawn(spawnLocation, Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(effect);
                firework.setFireworkMeta(meta);
            }
        }

        timer.setVisible(false); // Make the timer invisible.
    }

    /**
     * Cancels the Deathswap with no winners.
     * This will never be triggered automatically and can only be triggered by Commands.
     *
     * @see Deathswap#stop() To stop the Deathswap normally and let someone winner.
     */
    public void cancel() {
        this.started = false;
        this.task.cancel();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(config.deathswapCancelled);
        });

        timer.setVisible(false);
        Debug.log("Deathswap cancelled.");
    }

    /**
     * Swaps all Players in the List.
     */
    private void swap() {
        /* Comment left from 7th July 2021 -
        // What do we do here?! //
        // If there are Two Players we need to simply swap their locations (We would need to store the Location of the first Player).
        // But what if there are more than 2?
        // 1. Cycling Player[n] to Player[n+1] seems like a good idea but Players would always be swapped to the same other Player.
        // This works but has flaws.
        // 2. What if we separate the Players into groups of 2 and actually swap them?
        // What if there are an odd number of Players?
        // 3. What if we swap each Player randomly?
        // As with idea 1 this isn't really a swap but that doesn't matter.
        // This may be the best idea but would be a nightmare to implement.
        // We would need to keep track of already teleported Players and their previous locations.
        // This could be partially by cloning the list of Players by value (Not by reference!).
        // This however would only account for either already teleported Players or their previous locations. Not both.
        // I don't like this solution though I'll have to call it quits for the time being.
        // It's 5:49 AM I haven't slept in a while and I'm trying to solve algorithmic problems.
        // help
        */

        Debug.log("Swapping Players!");

        if (swapPlayersForward) {
            // Where the First Player was prior to Teleporting. (Used to teleport the last Player)
            Location player1Start = players.get(0).getLocation();

            // I want to merge these 2 statements but they are just too different :(

            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);

                if (i == players.size() - 1) {
                    player.teleport(player1Start);
                } else {
                    Player swapTarget = players.get(i + 1);
                    player.teleport(swapTarget.getLocation());
                }
            }
        } else {
            // Where the First Player was prior to Teleporting. (Used to teleport the last Player)
            Location finalPlayerDestination = players.get(players.size() - 1).getLocation();

            for (int i = players.size() - 1; i > -1; i--) {
                Player player = players.get(i);

                if (i == 0) {
                    player.teleport(finalPlayerDestination);
                } else {
                    Player swapTarget = players.get(i - 1);
                    player.teleport(swapTarget.getLocation());
                }
            }
        }

        // Swap the Players in the opposite direction next time.
        swapPlayersForward = !swapPlayersForward;
    }

    /**
     * Schedules the next swap using the settings specified in the Config.
     *
     * @see Deathswap#initialSwapTime For the Amount of Time in Seconds this round will last.
     * @see Deathswap#timeRemaining For the Amount of Time in Seconds that remains to the next Nwap.
     */
    private void scheduleNextSwap() {
        if(config.swapIntervalVariation != 0) {
            int interval = config.swapInterval;
            int variation = config.swapIntervalVariation;

            // This line doesn't look particularly nice :(
            // If the Swap Interval Variation is 120 Seconds or 2 Minutes that means that
            // this line will generate a random number between 0 and 240 (1 is added because Java would usually generate between 0 and 239).
            // we will then subtract 120 from this random number getting some number between -120 and 120.
            // Finally we add this number to the swap interval.
            // If the Swap Interval was set to 300 Seconds or 5 Minutes this would mean that the next swap will occur in anywhere between 3 and 7 Minutes.
            initialSwapTime = interval + random.nextInt(variation * 2 + 1) - variation;
            timeRemaining = initialSwapTime;
        }
        else {
            // If the Swap Interval Variation is disabled just set it to the Swap Interval.
            initialSwapTime = config.swapInterval;
            timeRemaining = initialSwapTime;
        }

        Debug.log("Scheduled next swap to occur in " + initialSwapTime + " seconds.");
    }

    /**
     * Checks if the Deathswap is currently ongoing.
     * @return True if the Deathswap is ongoing, False otherwise.
     */
    public boolean hasStarted() {
        return this.started;
    }

    /**
     * Gets the Deathswap's Config Settings.
     * @return The Deathswap's Config.
     */
    public Config getConfig() {
        return this.config;
    }

    /**
     * Sets the entire Deathswap Config.
     * Almost exclusively used by the "/deathswap config reset all" command.
     * @param config The Config to set it to.
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    public int getInitialSwapTime() {
        return this.timeRemaining;
    }

    public int getTimeRemaining() {
        return this.timeRemaining;
    }

    /**
     * Gets a List of all Players, not including dead ones.
     * @return The List of all Players.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

}
