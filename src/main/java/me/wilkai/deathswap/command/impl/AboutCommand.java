package me.wilkai.deathswap.command.impl;

import me.wilkai.deathswap.command.AbstractCommand;
import me.wilkai.deathswap.command.CommandInfo;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;

public class AboutCommand extends AbstractCommand {

    private static final TextComponent aboutMessage;

    public AboutCommand() {
        super(new CommandInfo("about")
                .setSummary("Displays information about the Deathswap Plugin.")
                .setRequiresOp(false));
    }

    static {
        aboutMessage = new TextComponent("§e§lDeathswap §r§eby Wilkai\n");
        aboutMessage.addExtra("§7Kill your enemies by swapping them into a deadly position.\n");

        aboutMessage.addExtra("\n§fLinks:");

        String page = "https://www.bible.com";
        String repo = "https://www.github.com/wilkaki/deathswap";
        String wiki = "https://www.github.com/wilkaki/deathswap/wiki";

        TextComponent pageLink = new TextComponent("\n §9§nCurseforge");
        pageLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§e" + page)));
        pageLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, page));

        TextComponent repoLink = new TextComponent("\n §9§nGithub");
        repoLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§e" + repo)));
        repoLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, repo));

        TextComponent wikiLink = new TextComponent("\n §9§nWiki");
        wikiLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§e" + wiki)));
        wikiLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, wiki));

        TextComponent wilkaiLink = new TextComponent("\n\n§c♥ Wilkai");
        wilkaiLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Wilkai")));
        wilkaiLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/Wilkai"));

        aboutMessage.addExtra(pageLink);
        aboutMessage.addExtra(repoLink);
        aboutMessage.addExtra(wikiLink);
        aboutMessage.addExtra(wilkaiLink);
    }

    public void execute(CommandSender sender, String[] args) {
        sender.spigot().sendMessage(aboutMessage);
    }
}
