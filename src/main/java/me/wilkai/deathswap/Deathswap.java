package me.wilkai.deathswap;

import me.wilkai.deathswap.config.Config;
import org.bukkit.*;
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

    private final Random random;
    private final DeathswapPlugin plugin;
    private final ArrayList<Player> players;

    /**
     * The config settings of the deathswap, controls the game's behaviour.
     * Called settings because methods such has "getConfig" are contained in the Superclasses.
     */
    private Config config;

    /**
     * This task is executed every 20 Ticks (1 Second) and counts down to the next swap.
     */
    private BukkitTask swapTask;

    /**
     * Is the Deathswap ongoing?
     */
    private boolean started;

    /**
     * Time in seconds until the Grace Period ends.
     */
    private int gracePeriodRemaining;

    /**
     * The time in seconds until the next swap.
     */
    private int timeRemaining;

    /**
     * If True when swapping Player[n] will be teleported to Player[n + 1]
     * If False when swapping Player[n] will be teleported to Player[n - 1]
     */
    private boolean swapNext;

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
     * Starts the Deathswap.
     */
    public void start() {
        this.started = true;
        this.gracePeriodRemaining = config.gracePeriod;
        this.scheduleNextSwap();

        this.swapTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(gracePeriodRemaining > 0) {
                    gracePeriodRemaining--;

                    if(gracePeriodRemaining % 60 == 0) {
                        String message; // Message that is sent to everyone.
                        int minutesLeft = gracePeriodRemaining / 60;

                        if(minutesLeft == 0) {
                            message = "§cGrace Period is over!";
                        }
                        else if(minutesLeft == 1) {
                            message = "§a1 Minute until Grace Period ends.";
                        }
                        else {
                            message = "§a" + minutesLeft + " Minutes until Grace Period ends.";
                        }

                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
                    }

                    if(gracePeriodRemaining <= 5 && gracePeriodRemaining != 0) {
                        String message = String.format("§eGrace Period ends in %d", gracePeriodRemaining);

                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
                    }

                    return;
                }

                timeRemaining--;

                if(timeRemaining <= config.beginCountdownTime && timeRemaining != 0) { // If it isn't time to swap yet but it will be soon.
                    String message = String.format("§cSwapping in %d.", timeRemaining);

                    if(config.countdownWithTitles) {
                        players.forEach(player -> player.sendTitle("§c" + timeRemaining, "", 0, 25, 0));
                    }
                    else if(config.playSounds) {
                        players.forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f));
                    }

                    Bukkit.broadcastMessage(message);
                }
                else if(timeRemaining == 0) { // If it's time to swap
                    String message = "§aSwapping!";

                    if(config.countdownWithTitles) {
                        players.forEach(player -> player.sendTitle("§aSwap!", "", 0, 20, 10));
                    }
                    else if(config.playSounds) {
                        players.forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f));
                    }

                    Bukkit.broadcastMessage(message);
                    scheduleNextSwap(); // Schedule the Next Swap before executing the Current One in order to avoid timing issues.
                    swap(); // Execute the swap.
                }

            }
        }.runTaskTimer(plugin, 0, 20);
    }

    /**
     * Stops the Deathswap.
     */
    public void stop() {
        if(players.size() == 0) { // Tie!
            String message = "§cTie!";
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendTitle(message, "§6Nobody Won :(", 40, 100, 10);
            });

            this.started = false;
            this.swapTask.cancel();
        }
        else if(players.size() > 1) { // Wrong Usage!
            // If there is still more than 1 Player then the Deathswap has not ended yet.
            return;
        }
        else {
            this.started = false;
            this.swapTask.cancel();

            Player winner = players.get(0); // Get the Winner.
            String message = ChatColor.GOLD + winner.getName() + ChatColor.YELLOW + "wins!";

            Bukkit.getOnlinePlayers().forEach(player -> {
                if(player != winner) {
                    player.teleport(winner.getLocation());
                    player.sendTitle(message, "§eSucks to be you lmao.", 60, 120, 30);
                }
                else {
                    player.sendTitle(ChatColor.GOLD + "You " + ChatColor.YELLOW + " won!", "§eGood Job! :)", 70, 130, 40);
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
                FireworkEffect.Type type = FireworkEffect.Type.values()[random.nextInt(5)];

                // Generate a random Firework Effect.
                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(color)
                        .flicker(random.nextBoolean())
                        .trail(random.nextBoolean())
                        .with(type)
                        .build();

                Location spawnLocation = new Location(world, x, location.getY(), z);

                Firework firework = world.spawn(spawnLocation, Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(effect);
                firework.setFireworkMeta(meta);
            }
        }

        Bukkit.getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.SURVIVAL));
    }

    public void cancel() {
        this.started = false;
        this.swapTask.cancel();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage("§cDeathswap has been cancelled! :(");
        });

    }

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

        if(players.size() == 2) {
            Location player1Origin = players.get(0).getLocation();
            Player player1 = players.get(0);
            Player player2 = players.get(1);

            player1.teleport(player2.getLocation());
            player2.teleport(player1Origin);
        }
        else {
            if(swapNext) {
                // Where the First Player was prior to Teleporting. (Used to teleport the last Player)
                Location player1Start = players.get(0).getLocation();

                for(int i = 0; i < players.size(); i++) {
                    Player player = players.get(i);

                    if(i == players.size() - 1) {
                        player.teleport(player1Start);
                    }
                    else {
                        Player swapTarget = players.get(i + 1);
                        player.teleport(swapTarget.getLocation());
                    }
                }
            }
            else {
                // Where the First Player was prior to Teleporting. (Used to teleport the last Player)
                Location finalPlayerDestination = players.get(players.size() - 1).getLocation();

                for(int i = players.size() - 1; i > -1; i--) {
                    Player player = players.get(i);

                    if(i == 0) {
                        player.teleport(finalPlayerDestination);
                    }
                    else {
                        Player swapTarget = players.get(i - 1);
                        player.teleport(swapTarget.getLocation());
                    }
                }
            }
        }

        swapNext = !swapNext;
    }

    /**
     * Schedules the next swap using the settings specified in the Config.
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
            timeRemaining = interval + random.nextInt(variation * 2 + 1) - variation;
        }
        else {
            // If the Swap Interval Variation is disabled just set it to the Swap Interval.
            timeRemaining = config.swapInterval;
        }
    }

    /**
     * Checks if the Deathswap is currently ongoing.
     * @return True if the Deathswap is ongoing, False otherwise.
     */
    public boolean hasStarted() {
        return this.started;
    }

    public Config getConfig() {
        return this.config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * Gets a List of all Players, not including dead ones.
     * @return The List of all Players.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

}
