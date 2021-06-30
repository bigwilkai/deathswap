package com.wilkei.deathswap.command;

import com.wilkei.deathswap.Deathswap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract command which all Deathswap commands must inherit.
 */
public abstract class AbstractCommand {

    /**
     * The Instance of the Deathswap Plugin.
     * Used for getting other variables such as the Settings or Command Handler.
     */
    @NotNull
    public final Deathswap deathswap;

    /**
     * The name of this command, this is added onto the end of the plugin command to form the actual command.
     * In the form: /deathswap + [name]
     * As an example: /deathswap config
     */
    @NotNull // A command can't exist without a name because that's not how that works.
    public final String name;

    /**
     * The Summary / Description of this command. This is used by the help command to list all commands and describe what they do.
     */
    @NotNull
    public final String summary;

    /**
     * Alternative names that can be used to reference this command.
     * These are added to the plugin command in the exact same way as this command's actual name.
     */
    @NotNull // A value will be assigned to this automatically if the input value is null.
    public final List<String> aliases;

    /**
     * Creates a new instance of the Abstract Command class with a give name and set of aliases.
     * @param name The name of this command, Must not be null or empty.
     * @param aliases The aliases of this command, (Optional).
     */
    public AbstractCommand(@NotNull Deathswap deathswap, @NotNull String name, @NotNull String summary, @Nullable List<String> aliases) {
        this.deathswap = deathswap;
        this.name = name;
        this.summary = summary;

        if(aliases == null) { // Makes sure that the Command Handler doesn't accidentally throw a Null Pointer Exception whilst checking through aliases.
            this.aliases = new ArrayList<>();
        }
        else {
            this.aliases = aliases;
        }
    }

    /**
     * Method which is called whenever this Subcommand is executed.
     * /deathswap [name] [args...]
     *
     * @param sender The thing that executed the command. Could be an Entity, the Console, a Block, or something else that I forgot about.
     * @param command The Bukkit Command which is being executed.
     * @param label The Alias of the Command which was used.
     * @param args All arguments provided to this specific command. "Arguments" are values provided after the plugin command and the base command.
     *             e.g /deathswap config [swapInterval] [500]
     *             where the values in brackets are arguments.
     */
    public abstract void execute(CommandSender sender, Command command, String label, String[] args);

    /**
     * Gets the autocomplete list of this Command.
     * @param sender The thing/person/object that executed the command.
     * @param command The Command which is being executed.
     * @param alias The Alias of the Command which was used.
     * @param args The arguments provided to this specific command, works the same as with the execute() method.
     * @return The list of autocomplete options to display to the user.
     */
    public abstract List<String> complete(CommandSender sender, Command command, String alias, String[] args);

    /**
     * Checks if a specific string matches this command.
     * @param command The command to check the validity of.
     * @return True if the provided string is equal to the name of this command or contained in the aliases, False otherwise.
     */
    public final boolean matches(String command) { // Protip: The final keyword in a method prevents it from being overridden by a subclass.
        return this.name.equals(command) || this.aliases.contains(command); // Checks if the command matches this command's name or is contained within this command's aliases.
    }
}
