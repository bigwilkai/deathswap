package me.wilkai.deathswap.command;

import me.wilkai.deathswap.config.Config;
import me.wilkai.deathswap.config.ConfigElement;
import me.wilkai.deathswap.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Command Used for Easily Changing Config Settings.
 * Uses a fuckton of Reflection because I though it would be a great idea.
 */
public class ConfigCommand extends AbstractCommand {

    public ConfigCommand() {
        super("config", "Gets and Sets config settings.", null, true);
    }

    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0) {
            if(args[0].equals("help")) { // If the user typed /deathswap config help.
                StringBuilder builder = new StringBuilder();
                builder.append("§e===== Config Help =======\n");
                builder.append("§6/deathswap config ...\n");

                for(Field field : Config.class.getFields()) {
                    ConfigElement element = field.getAnnotation(ConfigElement.class);

                    if(element != null) { // Every field in the Config class has to contain a summary so we need to get that.
                        // Example: (Aqua) swapInterval - (Italic) The duration between swaps. (Reset)
                        builder.append(" ");
                        builder.append(field.getName());
                        builder.append(" - §7");
                        builder.append(element.summary());
                        builder.append("§r\n\n");
                    }
                }

                builder.append("§e=======================");
                sender.sendMessage(builder.toString());
            }
            else {
                if(args.length > 1) {
                    try {
                        if(!(args[0].equals("get") || args[0].equals("set") || args[0].equals("reset"))) {
                            sender.sendMessage("§cUnknown Argument! Valid options are §ehelp§c, §eget§c, §eset§c or §ereset§c.");
                            return;
                        }

                        if(args[0].equals("reset") && args[1].equals("all")) {
                            sender.sendMessage("Reset all Config Settings to Default.");
                            plugin.getDeathswap().setConfig(new Config());
                            return;
                        }

                        Field field = Config.class.getField(args[1]);
                        ConfigElement element = field.getAnnotation(ConfigElement.class);

                        if(element == null) {
                            throw new NoSuchFieldException("");
                        }

                        if(args[0].equals("get")) {
                            Class<?> type = field.getType();
                            String value = field.get(plugin.getDeathswap().getConfig()).toString();
                            String message = element.name() + " is currently set to ";

                            if (value == null) { // If the value is null set the Color to Grey.
                                message += "§7null";
                            } else if (type.equals(boolean.class)) { // Color the value Green if it is True, and Red otherwise.
                                if (value.equals("true")) {
                                    message += "§a" + value;
                                } else {
                                    message += "§c" + value;
                                }
                            } else if (type.equals(short.class) || type.equals(int.class) || type.equals(long.class) || type.equals(float.class) || type.equals(double.class)) { // Color the value Aqua.
                                message += "§b" + value;
                            } else if (type.equals(String.class)) { // Put Quotation marks around the value and color it Yellow.
                                message += "\"§e" + value + "\"";
                            } else {
                                message += value;
                            }

                            message += "§r.";
                            sender.sendMessage(message);
                        }
                        else if(args[0].equals("set")) {
                            Class<?> type = field.getType();
                            String message = "Set " + element.name() + " to ";

                            if (type.equals(boolean.class)) {
                                boolean value;
                                if (args[2].equalsIgnoreCase("true")) {
                                    value = true;
                                    message += "§atrue";
                                } else if (args[2].equalsIgnoreCase("false")) {
                                    value = false;
                                    message += "§cfalse";
                                } else {
                                    sender.sendMessage("§c" + element.name() + " cannot be set to " + args[2] + " it can only be set to true or false.");
                                    return;
                                }

                                field.setBoolean(plugin.getDeathswap().getConfig(), value);
                            } else if (type.equals(float.class) || type.equals(double.class)) {
                                try {
                                    double value = Double.parseDouble(args[2]);

                                    if (value > element.max()) {
                                        sender.sendMessage("§c" + element.name() + " cannot be set any higher than " + element.max() + ".");
                                        return;
                                    } else if (value < element.min()) {
                                        sender.sendMessage("§c" + element.name() + " cannot be set any lower than " + element.min() + ".");
                                        return;
                                    }

                                    field.set(plugin.getDeathswap().getConfig(), value);
                                    message += "§b" + value;
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§c" + element.name() + " expects a double but §e" + args[2] + "§c is not a double.");
                                    return;
                                }
                            } else if (type.equals(short.class)) {
                                try {
                                    short value = Short.parseShort(args[2]);

                                    if (value > element.max()) {
                                        sender.sendMessage("§c" + element.name() + " cannot be set any higher than " + element.max() + ".");
                                        return;
                                    } else if (value < element.min()) {
                                        sender.sendMessage("§c" + element.name() + " cannot be set any lower than " + element.min() + ".");
                                        return;
                                    }

                                    message += "§b" + value;

                                    field.setShort(plugin.getDeathswap().getConfig(), value);
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§c" + element.name() + " is supposed to be a number but §e" + args[2] + "§c is not a number.");
                                    return;
                                }
                            } else if (type.equals(int.class)) {
                                try {
                                    int value = Integer.parseInt(args[2]);

                                    if (value > element.max()) {
                                        sender.sendMessage("§c" + element.name() + " cannot be set any higher than " + element.max() + ".");
                                        return;
                                    } else if (value < element.min()) {
                                        sender.sendMessage("§c" + element.name() + " cannot be set any lower than " + element.min() + ".");
                                        return;
                                    }

                                    message += "§b" + value;

                                    field.setInt(plugin.getDeathswap().getConfig(), value);
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§c" + element.name() + " is supposed to be a number but §e" + args[2] + "§c is not a number.");
                                    return;
                                }
                            } else if (type.equals(long.class)) {
                                try {
                                    long value = Long.parseLong(args[2]);

                                    if (value > element.max()) {
                                        sender.sendMessage("§c" + element.name() + " cannot be set any higher than " + element.max() + ".");
                                        return;
                                    } else if (value < element.min()) {
                                        sender.sendMessage("§c" + element.name() + " cannot be set any lower than " + element.min() + ".");
                                        return;
                                    }

                                    message += "§b" + value;

                                    field.setLong(plugin.getDeathswap().getConfig(), value);
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§c" + element.name() + " is supposed to be a number but §e" + args[2] + "§c is not a number.");
                                    return;
                                }
                            } else { // This is not a catch-all case as it only supports Strings.
                                try {
                                    field.set(plugin.getDeathswap().getConfig(), args[2]);
                                    message += "§e\"" + args[2] + "\"";
                                } catch (IllegalArgumentException e) {
                                    sender.sendMessage("§c" + type.getTypeName() + " is not supported by Deathswap's Config.");
                                    return;
                                }
                            }

                            message += "§r.";
                            sender.sendMessage(message);
                        }
                        else { // Reset
                            Config defaultConfig = new Config();

                            for(Field defaultField : defaultConfig.getClass().getFields()) {
                                if(defaultField.getName().equals(args[1])) {
                                    Object state = defaultField.get(defaultConfig);
                                    field.set(plugin.getDeathswap().getConfig(), state);

                                    Class<?> type = field.getType();
                                    String value = field.get(plugin.getDeathswap().getConfig()).toString();
                                    String message = "Set " + element.name() + " to its default value (";

                                    if (value == null) { // If the value is null set the Color to Grey.
                                        message += "§7null";
                                    } else if (type.equals(boolean.class)) { // Color the value Green if it is True, and Red otherwise.
                                        if (value.equals("true")) {
                                            message += "§a" + value;
                                        } else {
                                            message += "§c" + value;
                                        }
                                    } else if (type.equals(short.class) || type.equals(int.class) || type.equals(long.class) || type.equals(float.class) || type.equals(double.class)) { // Color the value Aqua.
                                        message += "§b" + value;
                                    } else if (type.equals(String.class)) { // Put Quotation marks around the value and color it Yellow.
                                        message += "\"§e" + value + "\"";
                                    } else {
                                        message += value;
                                    }

                                    message += "§r).";
                                    sender.sendMessage(message);

                                    return;
                                }
                            }
                        }
                    }
                    catch (NoSuchFieldException e) {
                        List<String> settings = new ArrayList<>();

                        for(Field field : Config.class.getFields()) {
                            if(field.getAnnotation(ConfigElement.class) == null) {
                                continue;
                            }
                            settings.add(field.getName());
                        }

                        String closestMatch = StringUtils.closestMatch(args[1], settings.toArray(new String[1]));
                        int distance = StringUtils.stringDistance(closestMatch, args[1]);

                        String message;
                        if(distance < 5) {
                            message = "§cDon't know what " + args[1] + " is!\n"
                                    + "Did you mean §e" + closestMatch + "§c?";

                        }
                        else {
                            message = "§cDon't know what " + args[1] + " is!\n"
                                    + "Type §e/deathswap config help§c for a list of config settings.";

                        }
                        sender.sendMessage(message);
                    }
                    catch(IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    sender.sendMessage("§cYou must specify a Config Setting.");
                }
            }
        }
        else {
            this.execute(sender, command, label, new String[] {"help"}); // Show the user the list of config settings.
        }
    }

    public List<String> complete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> autocomp = new ArrayList<>();

        if(args.length == 1) {
            autocomp.add("help");
            autocomp.add("get");
            autocomp.add("set");
            autocomp.add("reset");
        }
        else if(args.length == 2) {
            switch(args[0]) {
                case("reset"):
                    autocomp.add("all");
                case("get"):
                case("set"):
                    Field[] fields = Config.class.getFields();

                    for(Field field : fields) {
                        if(field.getAnnotation(ConfigElement.class) == null) {
                            continue;
                        }
                        autocomp.add(field.getName());
                    }
            }
        }
        else if(args.length == 3 && args[0].equals("set")) {
            try {
                Field field = Config.class.getField(args[1]);

                if(field.getType().equals(boolean.class)) {
                    autocomp.add("true");
                    autocomp.add("false");
                }
            }
            catch (NoSuchFieldException e) {
                e.printStackTrace();
                // Field cannot be found, don't autocomplete.
            }
        }

        return autocomp;
    }
}
