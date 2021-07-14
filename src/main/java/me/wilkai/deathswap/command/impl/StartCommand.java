package me.wilkai.deathswap.command.impl;

import me.wilkai.deathswap.Deathswap;
import me.wilkai.deathswap.command.AbstractCommand;
import me.wilkai.deathswap.command.CommandInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class StartCommand extends AbstractCommand {

    public StartCommand() {
        super(new CommandInfo("start").setSummary("Starts the Deathswap"));
    }

    public void execute(CommandSender sender, String[] args) {
        Deathswap deathswap = plugin.getDeathswap();

        if(deathswap.hasStarted()) {
            String message = """
                    §cDeathswap has already started!
                    If you want to restart it type §6/deathswap §ecancel§c.
                    """;
            sender.sendMessage(message);
        }
        else if(deathswap.getPlayers().size() < 2) {
            String message = """
                    §cYou must have at least 2 players to start!\s
                    Add Players by using §e/deathswap players add <player>§c.""";

            sender.sendMessage(message);
        }
        else {
            sender.sendMessage("§aStarting Deathswap!");
            deathswap.start();
        }
    }
}
