package me.wilkai.deathswap.util;

import me.wilkai.deathswap.Deathswap;
import me.wilkai.deathswap.DeathswapPlugin;
import org.bukkit.Bukkit;

public class Debug {

    private static final Deathswap deathswap;

    static {
        deathswap = DeathswapPlugin.getInstance().getDeathswap();
    }

    /**
     * Sends a Message to all Operators (Given Debug Mode is Enabled).
     * @param message The message to send.
     */
    public static void log(String message) {
        if(deathswap.getConfig().debugMode) {
            String styledMessage = "§8§o[Debug: " + message + "]";

            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(styledMessage));
        }
    }
}
