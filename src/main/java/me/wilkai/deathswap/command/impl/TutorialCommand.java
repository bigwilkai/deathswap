package me.wilkai.deathswap.command.impl;

import me.wilkai.deathswap.command.AbstractCommand;
import me.wilkai.deathswap.command.CommandInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;

/**
 * Explains to the Player how Deathswap works and how they can start it!
 */
public class TutorialCommand extends AbstractCommand {

    public TutorialCommand() {
        super(new CommandInfo("tutorial")
                .setSummary("Explains how to start Deathswap.")
                .withAlias("helpmeiamconfused")
                .setRequiresOp(false));
    }

    public void execute(CommandSender sender, String[] args) {
        TextComponent message = new TextComponent();
        message.addExtra("> Deathswap Tutorial <\n\n");
        message.addExtra("Type /help or click ");

        TextComponent helpCommand = new TextComponent("§e§nhere");
        helpCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to List Commands!")));
        helpCommand.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathswap help"));

        message.addExtra(helpCommand);
        message.addExtra("§r to get a list of commands.\n\n");

        message.addExtra("§lStarting >\n");
        message.addExtra("First add atleast 2 Players to the game using ");

        TextComponent addPlayerCommand = new TextComponent("§e§n/deathswap players add <player>");
        addPlayerCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to run /deathswap players add <player>")));
        addPlayerCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/deathswap players add "));

        message.addExtra(addPlayerCommand);
        message.addExtra(".\n");

        message.addExtra("Then to start run ");

        TextComponent startCommand = new TextComponent("§e§n/deathswap start");
        startCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to run /deathswap start")));
        startCommand.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathswap start"));

        message.addExtra(startCommand);
        message.addExtra("!\n\n");

        message.addExtra("If you want to change the way Deathswap works use the ");

        TextComponent configCommand = new TextComponent("§e§n/deathswap config command");
        configCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to run /deathswap config command")));
        configCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/deathswap config command"));

        message.addExtra(configCommand);
        message.addExtra(".\n");

        message.addExtra("A list of config commands can be found ");

        TextComponent configOptions = new TextComponent("§e§nhere");
        configOptions.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to list Config Settings.")));
        configOptions.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathswap config help"));

        message.addExtra(configOptions);
        message.addExtra(".\n\n");

        message.addExtra("§lConcept >\n");
        message.addExtra("The concept of Deathswap is that you must kill all Players without attacking them.\n");
        message.addExtra("Instead making use of the Swaps that occur to force them into a situation which will kill them!");

        sender.spigot().sendMessage(message);
    }

}
