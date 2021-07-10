package me.wilkai.deathswap.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class HelpCommand extends AbstractCommand {

    public HelpCommand() {
        super("help", "Displays a list of deathswap commands and what they do.", null, false);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<AbstractCommand> commands = plugin.getCommandHandler().getCommands();

        StringBuilder b = new StringBuilder();
        b.append("§e===== Deathswap Commands =====\n");
        b.append("§6/deathswap ...\n");

        for(AbstractCommand cmd : commands) {
            if(!sender.isOp() && cmd.requiresOp) { // If this is an Operator-Only Command.
                continue;
            }

            b.append(" §b");
            b.append(cmd.name);
            b.append(" - §o");
            b.append(cmd.summary);
            b.append("§r\n");
        }

        b.append("§e==============================");

        sender.sendMessage(b.toString());
    }
}
