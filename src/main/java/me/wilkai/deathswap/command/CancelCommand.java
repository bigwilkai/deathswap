package me.wilkai.deathswap.command;

import me.wilkai.deathswap.Deathswap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CancelCommand extends AbstractCommand {

    public CancelCommand() {
        super("cancel", "Outright cancels the Deathswap.", null, true);
    }

    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Deathswap deathswap = plugin.getDeathswap();

        if(!deathswap.hasStarted()) {
            sender.sendMessage("§Can't cancel Deathswap as it has not started.");
        }
        else {
            sender.sendMessage("§aCancelling Deathswap...");
            deathswap.cancel();
        }
    }
}
