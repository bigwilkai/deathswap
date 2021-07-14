package me.wilkai.deathswap.command;

import me.wilkai.deathswap.DeathswapPlugin;
import me.wilkai.deathswap.command.AbstractCommand;
import me.wilkai.deathswap.util.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class responsible for handling plugin subcommands.
 */
public class CommandHandler implements TabExecutor {

    /**
     * A list of all registered commands.
     */
    private final ArrayList<AbstractCommand> commands;

    /**
     * Creates a new Instance of the Command Handler Class.
     */
    public CommandHandler() {
        this.commands = new ArrayList<>();
        DeathswapPlugin plugin = DeathswapPlugin.getInstance();

        PluginCommand command = plugin.getCommand("deathswap");

        if(command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
        else {
            throw new NullPointerException("Deathswap Plugin Command is null, did you forget to include it in the plugin.yml?");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0) {
            // Loop through all registered commands.
            for(AbstractCommand cmd : commands) {
                if(!sender.isOp() && cmd.requiresOp) { continue; }
                if(cmd.matches(args[0])) { // If a command is found which has a matching name/alias to the command issued by the user.
                    String[] arguments = Arrays.copyOfRange(args, 1, args.length); // Cut out the first argument. (Because it will always be the command's name or an alias.
                    cmd.execute(sender, arguments); // Execute the command.
                    return true; // Return true because we found a command with a matching name.
                }
            }

            // The user has entered an unknown command, find the closest match to what they entered.
            List<String> commandNames = new ArrayList<>();
            for(AbstractCommand cmd : commands) {
                if(!sender.isOp() && cmd.requiresOp) {
                    continue; // Don't ask the Player if they meant a Command they are not allowed to use.
                }

                commandNames.add(cmd.name);
                commandNames.addAll(cmd.aliases);
            }

            // Put all names & aliases in an array.
            String[] names = commandNames.toArray(new String[0]);
            String closestMatch = StringUtils.closestMatch(args[0], names); // Find the one that most closely matches the command issued by the user.
            int distance = StringUtils.stringDistance(args[0], closestMatch); // Get the difference between the Closest Match and Issued Command.

            if(distance < 5) { // If the Closest Match isn't to far from the Issued Command.
                String text = "§cNot sure what you meant by " + args[0] + "\n"
                               + "Did you mean ";

                StringBuilder b = new StringBuilder("/deathswap " + closestMatch);

                for(int i = 1; i < args.length; i++) {
                    b.append(" ").append(args[i]);
                }

                // The command we think the player meant, plus the arguments.
                String rebuiltCommand = b.toString();

                TextComponent message = new TextComponent(text);
                TextComponent suggestion = new TextComponent("§e/deathswap " + closestMatch);
                suggestion.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + rebuiltCommand)));
                suggestion.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, rebuiltCommand));
                suggestion.setColor(ChatColor.YELLOW);

                message.addExtra(suggestion);
                message.addExtra("§c?");

                // Bukkit doesn't have support for TextComponents so we use spigot here.
                sender.spigot().sendMessage(message); // Inform the user that we couldn't find a matching command an suggest the closest known command.
            }
            else { // If the Closest Match is nothing like the Issued Command.
                String text = "§cNot sure what you mean by " + args[0] + "\n"
                               + "Type ";

                TextComponent message = new TextComponent(text);
                TextComponent helpCommand = new TextComponent("§e/deathswap help");

                helpCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + "Click to list commands.")));
                helpCommand.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathswap help"));

                message.addExtra(helpCommand);
                message.addExtra("§c for a list of commands.");

                sender.spigot().sendMessage(message); // Inform the Sender that we have no idea what they want.
            }

            return true; // We've accounted for all incorrect usage cases here, no need to send the usage message.
        }
        else { // The Player hasn't specified a subcommand, send them the help message to inform them of the commands they can use.
            for(AbstractCommand cmd : commands) {
                if(cmd.matches("help")) {
                    cmd.execute(sender, args);
                    return true;
                }
            }

            // If we get here that means the user entered no subcommand and the help command doesn't exist, oh no.
            return false; // Sends the default (and pretty unhelpful) usage message.
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> autocomp = new ArrayList<>();

        if(args.length == 1) { // If the user has typed /deathswap ...
            for(AbstractCommand subcommand : commands) {
                if(!sender.isOp() && subcommand.requiresOp) {
                    continue; // Don't add Operator-Only commands if the Player is not an Operator.
                }

                autocomp.add(subcommand.name); // Add the names of all registered commands.
            }
        }
        else { // If the user has provided a second argument (/deathswap <subcommand> ...)
            String string = args[0];

            for(AbstractCommand cmd : commands) { // Loop through all known commands and try to find a match to the provided subcommand.
                if(!sender.isOp() && cmd.requiresOp) {
                    continue; // Don't add Operator-Only commands if the Player is not an Operator.
                }

                if(cmd.matches(string)) { // If we find a match, get its autocomplete options.
                    List<String> commandAutocomp = cmd.complete(sender, Arrays.copyOfRange(args, 1, args.length));

                    // Prevents a Null Pointer Exception from being thrown if the command doesn't return any tab completions.
                    if(commandAutocomp != null) {
                        autocomp.addAll(commandAutocomp);
                    }
                }
            }
        }

        return autocomp;
    }

    /**
     * Registers a Subcommand to the Command Handler.
     * @param command The command to register. (Not a Bukkit Command)
     */
    public void register(AbstractCommand command) {
        this.commands.add(command);
    }

    /**
     * Gets a list of all registered Subcommands.
     * @return The list of all registered Subcommands.
     */
    public ArrayList<AbstractCommand> getCommands() {
        return this.commands;
    }
}
