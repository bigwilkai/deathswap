package me.wilkai.deathswap.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayersCommand extends AbstractCommand {

    public PlayersCommand() {
        super("players", "Add, Remove and List Players in the Deathswap.", null, true);
    }

    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            String b = """
                    §cYou need to specify an Argument!\s
                    Valid arguments are:
                    §eadd <player> §c- Adds a Player to the list of Players.
                    §eremove <player> §c- Removes a Player from the list of Players.
                    §elist §c- Lists all Players in the list of Players.""";

            sender.sendMessage(b);
            return;
        }

        ArrayList<Player> players = plugin.getDeathswap().getPlayers();

        switch (args[0]) {
            case("add"):
                if(args.length > 1) {
                    Player player = Bukkit.getPlayer(args[1]);

                    if(player != null) {
                        if(players.contains(player)) {
                            sender.sendMessage("§c" + player.getName() + " has already been added!");
                            return;
                        }

                        players.add(player);

                        StringBuilder b = new StringBuilder();
                        b.append("§aAdded ");
                        b.append(player.getName());
                        b.append(" to Players!\n");

                        if(players.size() == 1) {
                            b.append("There is now 1 Player.");
                        }
                        else {
                            // There are now {x} Players.
                            b.append("There are now ").append(players.size()).append(" Players.");
                        }

                        sender.sendMessage(b.toString());
                    }
                    else { // Unknown Player
                        sender.sendMessage("§cCould not find a Player with the name " + args[1] + ".");
                        return;
                    }
                }
                break;

            case("remove"):
                if(args.length > 1) {
                    Player player = Bukkit.getPlayer(args[1]);

                    if(player != null) {
                        if(!players.contains(player)) {
                            sender.sendMessage("§c" + player.getName() + " is not in the List of Players.");
                            return;
                        }

                        players.remove(player);

                        StringBuilder b = new StringBuilder();
                        b.append("§aRemoved ");
                        b.append(player.getName());
                        b.append(" from Players.\n");

                        if(players.size() == 1) {
                            b.append("There is now 1 Player.");
                        }
                        else {
                            // There are now {x} Players.
                            b.append("There are now ").append(players.size()).append(" Players.");
                        }

                        sender.sendMessage(b.toString());
                    }
                    else { // Unknown Player
                        sender.sendMessage("§cCould not find a Player with the name " + args[1] + ".");
                        return;
                    }
                }
                break;

            case("list"):
                String message;

                if(players.size() == 0) {
                    message = "There are no Players :(";
                }
                else {
                    StringBuilder b = new StringBuilder();
                    b.append("Showing list of Players (");
                    b.append(players.size());
                    b.append("):\n");

                    for(Player player : plugin.getDeathswap().getPlayers()) {
                        b.append(player.getName());
                        b.append("\n");
                    }

                    message = b.toString();
                }

                sender.sendMessage(message);
                break;

            default:
                String b = "§cUnrecognised argument " + args[0] + ".\n" +
                        "Valid arguments are: \n" +
                        "§eadd <player> §c- Adds a Player to the list of Players.\n" +
                        "§eremove <player> §c- Removes a Player from the list of Players.\n" +
                        "§elist §c- Lists all Players in the list of Players.";

                sender.sendMessage(b);
        }
    }

    public List<String> complete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> autocomp = new ArrayList<>();

        if(args.length == 1) {
            autocomp.add("add");
            autocomp.add("remove");
            autocomp.add("list");
        }
        else if(args.length == 2 && args[0].equals("add") || args[0].equals("remove")) {
            ArrayList<Player> players = plugin.getDeathswap().getPlayers();

            for(Player player : Bukkit.getOnlinePlayers()) {
                if((args[0].equals("add") && players.contains(player)) || (args[0].equals("remove") && !players.contains(player))) {
                    continue;
                }

                autocomp.add(player.getName());
            }
        }

        return autocomp;
    }
}
