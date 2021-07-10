package me.wilkai.deathswap.command;

import me.wilkai.deathswap.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command which informs the user about what Deathswap is.
 */
public class AboutCommand extends AbstractCommand {

    public AboutCommand() {
        super("about", "Explains how Deathswap works!", null, false);
    }

    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Config config = plugin.getDeathswap().getConfig();

        String interval;

        if(config.swapIntervalVariation == 0) {
            interval = String.valueOf(config.swapInterval / 60);
        }
        else {
            int lower = (config.swapInterval - config.swapIntervalVariation) / 60;
            int upper = (config.swapInterval + config.swapIntervalVariation) / 60;

            interval = lower + " - " + upper;
        }

        String message = String.format("""
                §6§lAbout Deathswap -§r§e\s
                Deathswap is a Minigame where 2 or more Players must kill each other without actually attacking each other.
                \n
                Players will be swapped every %s minutes and they must kill by swapping other players into deadly places.
                Place such as:
                - Off a cliff.
                - Into a pool of lava.
                - Deep in a body of water.
                - The Void (§oNot really practical§r§e)
                - In a block
                - Miami
                 \n
                The Player who outlives / kills everyone Wins.
                You can change certain settings to fit your needs using - /deathswap config.
                 \n
                - Yours faithfully, Wilkai.
                """, interval);

        sender.sendMessage(message);
    }
}
