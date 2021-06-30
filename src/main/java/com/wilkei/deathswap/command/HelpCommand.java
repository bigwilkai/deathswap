package com.wilkei.deathswap.command;

import com.wilkei.deathswap.Deathswap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends AbstractCommand {

    public HelpCommand(Deathswap deathswap) {
        super(deathswap, "help", "Displays a list of deathswap commands and what they do.", null);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<AbstractCommand> commands = deathswap.getCommandHandler().getCommands();

        StringBuilder b = new StringBuilder();
        b.append("§e===== Deathswap Commands =====\n");
        b.append("§6/deathswap ...\n");

        for(AbstractCommand cmd : commands) {
            b.append(" §b");
            b.append(cmd.name);
            b.append(" - §o");
            b.append(cmd.summary);
            b.append("§r\n");
        }

        b.append("§e==============================");

        sender.sendMessage(b.toString());
    }

    @Override
    public List<String> complete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
