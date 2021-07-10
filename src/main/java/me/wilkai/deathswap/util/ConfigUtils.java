package me.wilkai.deathswap.util;

import me.wilkai.deathswap.DeathswapPlugin;
import me.wilkai.deathswap.config.Config;

import java.lang.reflect.Field;

public class ConfigUtils {

    private static final DeathswapPlugin plugin;

    static {
        plugin = DeathswapPlugin.getInstance();
    }

    /**
     * Reads the Config from the Plugin's config.yml file.
     */
    public static Config readConfig() {
        Config config = new Config();

        for(Field field : Config.class.getFields()) {
            try {
                Object value = plugin.getConfig().get(field.getName());

                if(value == null) { // If a value is set to null.
                    continue; // Don't set the field, it will resort to its default value.
                }

                field.set(config, value); // If all is well set the value.
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return config;
    }

    /**
     * Saves the Config to the Plugin's config.yml file.
     */
    public static void saveConfig(Config config) {
        Config defaultConfig = new Config();

        for(Field field : Config.class.getFields()) {
            try {
                Object value = field.get(config);

                if(value == null) { // If the value has somehow been set to null.
                    value = field.get(defaultConfig); // Reset it to the default value.
                }

                plugin.getConfig().set(field.getName(), value);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
