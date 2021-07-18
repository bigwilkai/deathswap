package me.wilkai.deathswap.command.impl;

import me.wilkai.deathswap.command.AbstractCommand;
import me.wilkai.deathswap.command.CommandInfo;
import me.wilkai.deathswap.config.Config;
import me.wilkai.deathswap.config.ConfigElement;
import me.wilkai.deathswap.util.ConfigUtils;
import me.wilkai.deathswap.util.StringUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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
        super(new CommandInfo("config")
                .addUsage("get <setting>")
                .addUsage("set <setting> <value>")
                .addUsage("reset <setting>")
                .addUsage("help")
                .setSummary("Gets and Sets config settings."));
    }

    public void execute(CommandSender sender, String[] args) {
        if(args.length > 0) {
            String operation = args[0];

            if(operation.equals("help")) { // If the user typed /deathswap config help.
                StringBuilder builder = new StringBuilder();
                builder.append("§e===== Config Help =======\n");
                builder.append("§6/deathswap config ...§r");

                for(Field field : Config.class.getFields()) {
                    ConfigElement element = field.getAnnotation(ConfigElement.class);

                    if(element != null) { // Every field in the Config class has to contain a summary so we need to get that.
                        // Example: (Aqua) swapInterval - (Italic) The duration between swaps. (Reset)
                        builder.append("\n ");
                        builder.append(field.getName());
                        builder.append(" - §7");
                        builder.append(element.summary());
                        builder.append("§r\n");
                    }
                }

                builder.append("§e=======================");
                sender.sendMessage(builder.toString());
            }
            else {
                if(args.length > 1) {
                    try {
                        if(!(operation.equals("get") || operation.equals("set") || operation.equals("reset"))) {
                            sender.sendMessage("§cUnknown Argument! Valid options are §ehelp§c, §eget§c, §eset§c or §ereset§c.");
                            return;
                        }

                        if(operation.equals("reset") && args[1].equals("all")) {
                            sender.sendMessage("Reset all Config Settings to Default.");
                            plugin.getDeathswap().setConfig(new Config());
                            return;
                        }

                        Field field = Config.class.getField(args[1]);
                        ConfigElement element = field.getAnnotation(ConfigElement.class);

                        if(element == null) {
                            throw new NoSuchFieldException("");
                        }

                        if(operation.equals("get")) {
                            Class<?> type = field.getType();
                            String value = field.get(plugin.getDeathswap().getConfig()).toString();
                            String message = element.name() + " is currently set to ";

                            if (value == null) { // If the value is null set the Color to Grey.
                                message += "§7null";
                            }
                            else if (type.equals(boolean.class)) { // Color the value Green if it is True, and Red otherwise.
                                message += (value.equals("true")) ? "§atrue" : "§cfalse";
                            }
                            else if (type.isPrimitive()) { // (Number) Color the value Aqua.
                                message += "§b" + value;
                            }
                            else if (type.equals(String.class)) { // Put Quotation marks around the value and color it Yellow.
                                message += "\"§e" + value + "\"";
                            }
                            else { // Value is of an unknown type; Color it white.
                                message += value;
                            }

                            message += "§r.";
                            sender.sendMessage(message);
                        }
                        else if(operation.equals("set")) {
                            Class<?> type = field.getType();
                            String message = "Set " + element.name() + " to ";

                            if(args.length < 3) {
                                sender.sendMessage("You must specify a config setting!");
                                return;
                            }

                            String input = args[2];

                            if (type.equals(boolean.class)) {
                                boolean value;
                                if (input.equalsIgnoreCase("true")) {
                                    value = true;
                                    message += "§atrue";
                                } else if (input.equalsIgnoreCase("false")) {
                                    value = false;
                                    message += "§cfalse";
                                } else {
                                    sender.sendMessage("§c" + element.name() + " cannot be set to " + input + " it can only be set to true or false.");
                                    return;
                                }

                                field.setBoolean(plugin.getDeathswap().getConfig(), value);
                            } else if (type.equals(float.class) || type.equals(double.class)) {
                                try {
                                    double value = Double.parseDouble(input);

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
                                    sender.sendMessage("§c" + element.name() + " expects a double but §e" + input + "§c is not a double.");
                                    return;
                                }
                            } else if (type.equals(short.class)) {
                                try {
                                    short value = Short.parseShort(input);

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
                                    sender.sendMessage("§c" + element.name() + " is supposed to be a number but §e" + input + "§c is not a number.");
                                    return;
                                }
                            } else if (type.equals(int.class)) {
                                try {
                                    int value = Integer.parseInt(input);

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
                                    sender.sendMessage("§c" + element.name() + " is supposed to be a number but §e" + input + "§c is not a number.");
                                    return;
                                }
                            } else if (type.equals(long.class)) {
                                try {
                                    long value = Long.parseLong(input);

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
                                    sender.sendMessage("§c" + element.name() + " is supposed to be a number but §e" + input + "§c is not a number.");
                                    return;
                                }
                            } else { // This is not a catch-all case as it only supports Strings.
                                try {
                                    field.set(plugin.getDeathswap().getConfig(), input);
                                    message += "§e\"" + input + "\"";
                                } catch (IllegalArgumentException e) {
                                    sender.sendMessage("§c" + type.getTypeName() + " is not supported by Deathswap's Config.");
                                    return;
                                }
                            }

                            message += "§r.";
                            sender.sendMessage(message);
                            ConfigUtils.saveConfig(plugin.getDeathswap().getConfig());
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
                                    ConfigUtils.saveConfig(plugin.getDeathswap().getConfig());

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

                        TextComponent message;
                        if(distance < 5) {
                            message = new TextComponent("§cDon't know what " + args[1] + " is!\n"
                                    + "Did you mean ");

                            TextComponent suggestion = new TextComponent("§e" + closestMatch);

                            String expectedCommand = "/deathswap config " + operation + " " + closestMatch;

                            if(args.length > 2) {
                                expectedCommand += " " + args[2];
                            }

                            suggestion.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§e" + expectedCommand)));
                            suggestion.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, expectedCommand));

                            message.addExtra(suggestion);
                            message.addExtra("§c?");
                        }
                        else {
                            message = new TextComponent("§cDon't know what " + args[1] + " is!\n"
                                    + "Type ");

                            TextComponent suggestion = new TextComponent("§e/deathswap config help");
                            suggestion.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eClick to list Config Settings!")));
                            suggestion.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathswap config help"));

                            message.addExtra(suggestion);
                            message.addExtra(" §cfor a list of config settings.");
                        }
                        sender.spigot().sendMessage(message);
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
            this.execute(sender, new String[] {"help"}); // Show the user the list of config settings.
        }
    }

    public List<String> complete(CommandSender sender, String[] args) {
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
