package me.wilkai.deathswap.command.impl;

import me.wilkai.deathswap.Deathswap;
import me.wilkai.deathswap.command.AbstractCommand;
import me.wilkai.deathswap.command.CommandInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CancelCommand extends AbstractCommand {

    public CancelCommand() {
        super(new CommandInfo("cancel")
                .setSummary("Outright cancels the Deathswap."));
    }

    public void execute(CommandSender sender, String[] args) {
        Deathswap deathswap = plugin.getDeathswap();

        if(!deathswap.hasStarted()) {
            sender.sendMessage("§cCan't cancel Deathswap as it has not started.");
        }
        else {
            sender.sendMessage("§aCancelling Deathswap...");
            deathswap.cancel();
        }
    }
}
