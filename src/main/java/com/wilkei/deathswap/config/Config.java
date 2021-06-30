package com.wilkei.deathswap.config;

public class Config {

    /**
     * The base interval between swaps (In Seconds).
     */
    @ConfigInt(min = 10, max = 100)
    public int swapInterval;

    /**
     * The maximum amount the time until the next swap can vary from the Swap Interval (+/-)
     */
    @ConfigInt(min = 0, max = 1)
    public int swapIntervalVariation;

    /**
     * Should we allow Players to attack each other?
     * Users may want to prevent pvp because the concept of Deathswap is that Players
     * have to swap their opponents into a situation that will kill them rather than
     * kill them using pvp skill.
     */
    public boolean allowPvp;

    /**
     * Should we allow Players to travel to the Nether?
     * Users may want to prevent this because the Nether can be reached very early on in the
     * Deathswap and it would require pretty insane luck to survive.
     */
    public boolean allowNether;

}
