package me.wilkai.deathswap;

import me.wilkai.deathswap.util.ConfigUtils;
import me.wilkai.deathswap.command.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of the Deathswap plugin which will be called by Spigot / Paper upon startup.
 * This class will not be called if the jar is executed manually, instead it will load the "Main" class.
 */
public final class DeathswapPlugin extends JavaPlugin {

    private static DeathswapPlugin INSTANCE;

    /**
     * Custom Command Handler which allows us to work with subcommands easier.
     */
    private CommandHandler handler;

    private Deathswap deathswap;

    @Override
    public void onEnable() {
        INSTANCE = this;

        // Move the config.yml file to the Plugin's Data Folder.
        // This will not overwrite the file if it already exists.
        this.saveDefaultConfig();
        this.saveResource("messages.yml", false);

        this.deathswap = new Deathswap(this, ConfigUtils.readConfig());
        this.handler = new CommandHandler();
        handler.register(new HelpCommand());
        handler.register(new StartCommand());
        handler.register(new CancelCommand());
        handler.register(new ConfigCommand());
        handler.register(new PlayersCommand());

        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {
        ConfigUtils.saveConfig(deathswap.getConfig()); // Save the current config to disk.
    }

    /**
     * Gets the Plugin's main Command Handler containing all Plugin Commands.
     * @return The Plugin's Command Handler.
     */
    public CommandHandler getCommandHandler() {
        return this.handler;
    }

    /**
     * Gets the current instance of the Deathswap.
     * @return The current instance of the Deathswap.
     */
    public Deathswap getDeathswap() {
        return this.deathswap;
    }

    public static DeathswapPlugin getInstance() {
        return INSTANCE;
    }
}
