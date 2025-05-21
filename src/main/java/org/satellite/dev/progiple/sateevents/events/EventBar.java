package org.satellite.dev.progiple.sateevents.events;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.self.LSConfig;

import java.util.function.Function;

@Getter
public class EventBar {
    private final KeyedBossBar bar;
    private final SateEvent sateEvent;
    private final String title;

    @SuppressWarnings("deprecation")
    public EventBar(SateEvent sateEvent, BarColor barColor, BarStyle barStyle, String title) {
        this.title = ColorManager.color(title);

        this.bar = Bukkit.createBossBar(NamespacedKey.randomKey(), this.title, barColor, barStyle);
        this.sateEvent = sateEvent;

        this.update();
    }

    public void update() {
        Location location = this.sateEvent.getLocation();
        String x = location == null ? "---" : String.valueOf(location.getBlockX());
        String y = location == null ? "---" : String.valueOf(location.getBlockY());
        String z = location == null ? "---" : String.valueOf(location.getBlockZ());
        String world = location == null ? "---" : location.getWorld().getName();

        String ruWorldName = LSConfig.getMessage(String.format("worlds.%s", world));
        String title = Utils.applyReplacements(this.title,
                "event-%-" + this.sateEvent.getName(),
                "left-%-" + this.sateEvent.getDelay().getLeftSeconds(),
                "x-%-" + x, "y-%-" + y, "z-%-" + z, "world-%-" + world,
                "ls-world-%-" + ruWorldName);

        Player player = this.bar.getPlayers().isEmpty() ? null : this.bar.getPlayers().get(0);
        this.bar.setTitle(player == null ? title : Utils.setPlaceholders(player, title));

        SateEvent.Delay delay = this.sateEvent.getDelay();
        this.bar.setProgress((double) delay.getLeftSeconds() / delay.getMax());
    }

    public void addPlayer(Player player, Function<Player, Boolean> function) {
        if (function == null || function.apply(player)) this.bar.addPlayer(player);
    }

    public void remove() {
        this.bar.removeAll();
        Bukkit.removeBossBar(this.bar.getKey());
    }
}
