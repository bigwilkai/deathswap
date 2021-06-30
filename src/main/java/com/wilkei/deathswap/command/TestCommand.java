package com.wilkei.deathswap.command;

import com.wilkei.deathswap.Deathswap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TestCommand extends AbstractCommand {

    public TestCommand(Deathswap deathswap) {
        super(deathswap, "test", "Test command for testing.", null);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("test");
    }

    @Override
    public List<String> complete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> autocomp = new ArrayList<>();

        if(args.length == 1) {
            autocomp.add("soijg");
            autocomp.add("9eihbn");
            autocomp.add("steve");
        }
        else if(args.length == 2 && args[0].equals("steve")) {
            autocomp.add("big");
            autocomp.add("small");
        }

        return autocomp;
    }
}
