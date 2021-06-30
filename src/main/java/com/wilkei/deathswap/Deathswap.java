package com.wilkei.deathswap;

import com.wilkei.deathswap.command.HelpCommand;
import com.wilkei.deathswap.command.TestCommand;
import com.wilkei.deathswap.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Main class of the Deathswap plugin which will be called by Spigot / Paper upon startup.
 * This class will not be called if the jar is executed manually, instead it will load the "Main" class.
 */
public final class Deathswap extends JavaPlugin {

    /**
     * Seconds remaining until next swap.
     */
    private int time;

    /**
     * Random Number Generator :)
     */
    private Random random;

    /**
     * The list of participants in the deathswap.
     */
    private List<Player> players;

    /**
     * The config settings of the deathswap, controls the game's behaviour.
     */
    private Config settings;

    /**
     * Custom Command Handler which allows us to work with subcommands easier.
     */
    private CommandHandler handler;

    /**
     * Is the deathswap ongoing?
     */
    private boolean hasStarted;

    @Override
    public void onEnable() {
        this.settings = new Config();
        this.players = new ArrayList<>();
        this.random = new Random();
        this.handler = new CommandHandler(this);

        handler.register(new TestCommand(this));
        handler.register(new HelpCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Config getSettings() {
        return this.settings;
    }

    public CommandHandler getCommandHandler() {
        return this.handler;
    }
}
