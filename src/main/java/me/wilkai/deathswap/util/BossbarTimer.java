package me.wilkai.deathswap.util;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

/**
 * A Timer Utility class based off Minecraft's Bossbar.
 */
public class BossbarTimer {

    private final BossBar bossbar;

    private double max;
    private CountType countType;

    public BossbarTimer(String title) {
        bossbar = Bukkit.createBossBar(title, BarColor.RED, BarStyle.SOLID);
        max = 100.0d;
        countType = CountType.COUNTDOWN;
    }

    public void addPlayer(Player player) {
        bossbar.addPlayer(player);
    }

    public void removePlayer(Player player) {
        bossbar.removePlayer(player);
    }

    public void removeAllPlayers() {
        bossbar.removeAll();
    }

    public void setTitle(String title) {
        bossbar.setTitle(title);
    }

    public void setColor(BarColor color) {
        bossbar.setColor(color);
    }

    public void setStyle(BarStyle style) {
        bossbar.setStyle(style);
    }

    public void setCountType(CountType type) {
        this.countType = type;
    }

    public void setVisible(boolean visible) {
        bossbar.setVisible(visible);
    }

    public void setProgress(double progress) {
        double percentage = Math.min(progress / max, 1.0d); // If the Progress exceeds 1.0 then Bukkit will throw an exception.

        if(this.countType == CountType.COUNTUP) {
            percentage = 1.0d - percentage;
        }

        bossbar.setProgress(percentage);
    }

    public void setMax(double max) {
        this.max = max;
    }

    public enum CountType {
        /**
         * The Timer will Decrease over time until empty.
         */
        COUNTDOWN,

        /**
         * The Timer will Increase over time until full.
         */
        COUNTUP
    }
}
