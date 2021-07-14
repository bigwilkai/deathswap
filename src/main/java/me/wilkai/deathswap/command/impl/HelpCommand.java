package me.wilkai.deathswap.command.impl;

import me.wilkai.deathswap.command.AbstractCommand;
import me.wilkai.deathswap.command.CommandInfo;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class HelpCommand extends AbstractCommand {

    public HelpCommand() {
        super(new CommandInfo("help")
                .setSummary("Displays a List of Commands and what they do.")
                .setRequiresOp(false));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ArrayList<AbstractCommand> commands = plugin.getCommandHandler().getCommands();

        TextComponent message = new TextComponent("§e===== Deathswap Commands ===== \n§6/deathswap ...\n");

        String name;
        String summary;
        StringBuilder usage;
        String field;
        for(AbstractCommand cmd : commands) {
            if(!sender.isOp() && cmd.requiresOp) { // If this is an Operator-Only Command.
                continue;
            }

            name = cmd.name;
            summary = cmd.summary;
            usage = new StringBuilder("§eUsage > §b/deathswap " + cmd.name);

            if(cmd.usages.size() > 0) {
                usage.append("...");

                for(String use : cmd.usages) {
                    usage.append("\n  ").append(use);
                }
            }

            field = " §b" + name + " - §o " + summary + "\n";

            if(cmd.requiresOp) {
                usage.insert(0, "§cRequires Operator!\n");
            }

            TextComponent component = new TextComponent(field);
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(usage.toString())));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/deathswap " + cmd.name));

            message.addExtra(component);
        }

        message.addExtra("§e==============================");

        sender.spigot().sendMessage(message);
    }
}
