package me.wilkai.deathswap.config;

import me.wilkai.deathswap.DeathswapPlugin;

/**
 * Configuration which contains rules for how the Plugin should behave.
 */
public class Config {

    /**
     * The base interval between swaps (In Seconds).
     */
    @ConfigElement(name = "Swap Interval", summary = "The duration between swaps.", min = 5, max = 40000000)
    public int swapInterval = 300;

    /**
     * The maximum amount the time until the next swap can vary from the Swap Interval (+/-)
     */
    @ConfigElement(name = "Swap Interval Variation", summary = "The amount (+/-) that the Swap Interval will vary.", min = 0, max = 30000000)
    public int swapIntervalVariation = 120;

    @ConfigElement(name = "Grace Period", summary = "Time in which no swap will happen.", min = 0, max = 40000000)
    public int gracePeriod = 300;

    @ConfigElement(name = "Mob Spawn Rate", summary = "The Percentage of Hostile Mobs that will be allowed to spawn.", min = 0, max = 100)
    public int mobSpawnRate = 100;

    /**
     * Should we allow Players to attack each other?
     * Users may want to prevent pvp because the concept of Deathswap is that Players
     * have to swap their opponents into a situation that will kill them rather than
     * kill them using pvp skill.
     */
    @ConfigElement(name = "Allow Pvp", summary = "Allow Players to Attack each other?")
    public boolean allowPvp = false;

    /**
     * Should we allow Players to travel to the Nether?
     * Users may want to prevent this because the Nether can be reached very early on in the
     * Deathswap and it would require pretty insane luck to survive.
     */
    @ConfigElement(name = "Allow Nether", summary = "Should Players be allowed to enter the Nether?")
    public boolean allowNether = false;

    @ConfigElement(name = "Show Timer", summary = "Displays a Timer at the Top of your screen of how long is left until the Next Swap.")
    public boolean showTimer = false;

    @ConfigElement(name = "Allow Pearl Skipping", summary = "Allow Players to teleport with an Ender Pearl which was thrown before the current swap.")
    public boolean allowPearlSkipping = true;

    public String portalLightDenied = "§7§oGod whispers to you: Sorry buddy but we can't have any of that funny portal business around here.";
    public String deathswapBegin = "§a§lThe Deathswap has begun!";
    public String gracePeriodWarning = "§a<minutes> Minutes until Grace Period ends.";
    public String gracePeriodFinalWarning = "§a1 Minute until Grace Period ends.";
    public String gracePeriodCountdown = "§eGrace Period ends in <seconds>";
    public String gracePeriodEnd = "§cGrace Period is over!";
    public String swappingSoon = "§cSwapping in <seconds>.";
    public String swappingNow = "§aSwapping!";
    public String deathswapTieTitle = "§cTie!";
    public String deathswapTieSubtitle = "§6Nobody Won :(";
    public String deathswapDefeatTitle = "§6<winner>§e wins!";
    public String deathswapDefeatSubtitle = "§eSucks to be you lmao.";
    public String deathswapVictoryTitle = "§6You§e won!";
    public String deathswapVictorySubtitle = "§eGood Job! :)";
    public String deathswapCancelled = "§cDeathswap has been cancelled! :(";

    public static Config getInstance() {
        return DeathswapPlugin.getInstance().getDeathswap().getConfig();
    }
}
