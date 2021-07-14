package me.wilkai.deathswap.command;

import java.util.ArrayList;
import java.util.List;

public class CommandInfo {

    private final String name;
    private final List<String> usages;
    private final List<String> aliases;
    private String summary;
    private boolean requiresOp;

    public CommandInfo(String name) {
        this.name = name;
        this.summary = "No Summary Provided.";
        this.usages = new ArrayList<>();
        this.aliases = new ArrayList<>();
        this.requiresOp = true;
    }

    public String getName() {
        return this.name;
    }

    public String getSummary() {
        return this.summary;
    }

    public List<String> getUsages() {
        return this.usages;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public boolean requiresOp() {
        return this.requiresOp;
    }

    public CommandInfo setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public CommandInfo addUsage(String usage) {
        this.usages.add(usage);
        return this;
    }

    public CommandInfo setRequiresOp(boolean requiresOp) {
        this.requiresOp = requiresOp;
        return this;
    }

    public CommandInfo withAlias(String alias) {
        this.aliases.add(alias);
        return this;
    }

}
