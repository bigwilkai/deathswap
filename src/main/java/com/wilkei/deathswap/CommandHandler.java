package com.wilkei.deathswap;

import com.wilkei.deathswap.command.AbstractCommand;
import com.wilkei.deathswap.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler implements TabExecutor {

    private final ArrayList<AbstractCommand> commands;

    public CommandHandler(Deathswap deathswap) {
        this.commands = new ArrayList<>();

        PluginCommand command = deathswap.getCommand("deathswap");

        if(command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
        else {
            throw new NullPointerException("Deathswap Plugin Command is null, did you forget to include it in the plugin.yml?");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length > 0) {
            for(AbstractCommand cmd : commands) {
                if(cmd.matches(args[0])) {
                    String[] arguments = Arrays.copyOfRange(args, 1, args.length);
                    cmd.execute(sender, command, label, arguments);
                    return true;
                }
            }

            // The user has entered an unknown command, find the closest match to what they entered.
            List<String> commandNames = new ArrayList<>();
            for(AbstractCommand cmd : commands) {
                commandNames.add(cmd.name);
                commandNames.addAll(cmd.aliases);
            }

            String[] names = commandNames.toArray(new String[0]);
            String closestMatch = StringUtils.closestMatch(args[0], names);
            int distance = StringUtils.stringDistance(args[0], closestMatch);

            if(distance < 5) {
                String message = "§cNot sure what you meant by " + args[0] + "\n"
                               + "Did you mean §e/deathswap " + closestMatch + "?";

                // TODO: Allow the player to click on the "did you mean" message to execute the closest matching command.

                sender.sendMessage(message);
            }
            else {
                String message = "§cNot sure what you mean by " + args[0] + "\n"
                               + "Type §e/deathswap help§c for a list of commands.";

                // TODO: Allow the player to click on "/deathswap help" to instantly execute the help command.

                sender.sendMessage(message);
            }

            return true; // We've accounted for all incorrect usage cases here, no need to send the usage message.
        }
        else { // The Player hasn't specified a subcommand, send them the help message to inform them of the commands they can use.
            for(AbstractCommand cmd : commands) {
                if(cmd.matches("help")) {
                    cmd.execute(sender, command, label, args);
                    return true;
                }
            }

            // If we get here that means the user entered no subcommand and the help command doesn't exist, oh no.
            return false; // Sends the default (and pretty unhelpful) usage message.
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> autocomp = new ArrayList<>();

        if(args.length == 1) {
            for(AbstractCommand cmd : commands) {
                autocomp.add(cmd.name);
            }
        }
        else {
            String string = args[0];

            for(AbstractCommand cmd : commands) {
                if(cmd.matches(string)) {
                    List<String> commandAutocomp = cmd.complete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));

                    // Prevents a Null Pointer Exception from being thrown if the command doesn't return any tab completions.
                    if(commandAutocomp != null) {
                        autocomp.addAll(commandAutocomp);
                    }
                }
            }
        }

        return autocomp;
    }

    public void register(AbstractCommand command) {
        this.commands.add(command);
    }

    public ArrayList<AbstractCommand> getCommands() {
        return this.commands;
    }
}
