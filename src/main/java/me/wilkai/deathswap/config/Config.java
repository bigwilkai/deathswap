package me.wilkai.deathswap.config;

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

    @ConfigElement(name = "Begin Countdown Time", summary = "The time at which Deathswap will begin sending a countdown in chat.", min = 0, max = 40000000)
    public int beginCountdownTime = 10;

    @ConfigElement(name = "Mob Spawning Percentage", summary = "The Percentage of Hostile Mobs that will spawn.", min = 0, max = 100)
    public int mobSpawningPercentage = 100;

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

    /**
     * If true and Pvp is Disabled then we will send a message to Players who try and attack others informing them that Pvp is disabled.
     */
    @ConfigElement(name = "Announce Pvp Disabled", summary = "If a Player tries to attack another Player we should let them know that Pvp is disabled.")
    public boolean announcePvpDisabled = false;

    /**
     * If true and Portals are Disabled then we will send a message to Players who try to light a Portal informing them that the nether is disabled.
     */
    @ConfigElement(name = "Announce Portals Disabled", summary = "If a Player tries to travel to the Nether we should inform them that it is disabled.")
    public boolean announcePortalsDisabled = true;

    @ConfigElement(name = "Play Sounds", summary = "Plays cool note block sounds during the countdown.")
    public boolean playSounds = true;

    @ConfigElement(name = "Countdown With Titles", summary = "Displays the Countdown in large text in the center of each Player's screen.")
    public boolean countdownWithTitles = true;

}
