package org.satellite.dev.progiple.sateevents.events;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.util.utilities.LunaBossBar;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.self.configuration.LSConfig;
import org.satellite.dev.progiple.sateevents.SateEvents;

import java.util.UUID;
import java.util.function.Predicate;

@Getter
public class EventBar extends LunaBossBar {
    private final SateEvent sateEvent;
    public EventBar(SateEvent sateEvent, BarColor barColor, BarStyle barStyle, String title) {
        super(title, barColor, barStyle, key());
        this.sateEvent = sateEvent;
        try { this.update(); } catch (Throwable e) { SateEvents.getInstance().warning(e.getLocalizedMessage()); }
    }

    public EventBar(@NotNull String title, SateEvent sateEvent) {
        super(title, key());
        this.sateEvent = sateEvent;
        try { this.update(); } catch (Throwable e) { SateEvents.getInstance().warning(e.getLocalizedMessage()); }
    }

    public EventBar(@NotNull String title, String strBarColor, String strBarStyle, SateEvent sateEvent) {
        super(title, strBarColor, strBarStyle, key());
        this.sateEvent = sateEvent;
        try { this.update(); } catch (Throwable e) { SateEvents.getInstance().warning(e.getLocalizedMessage()); }
    }

    @Override
    public LunaBossBar update() {
        Location location = this.sateEvent.getLocation();
        String x = location == null ? "---" : String.valueOf(location.getBlockX());
        String y = location == null ? "---" : String.valueOf(location.getBlockY());
        String z = location == null ? "---" : String.valueOf(location.getBlockZ());
        String world = location == null ? "---" : location.getWorld().getName();

        String ruWorldName = LSConfig.getMessage(String.format("worlds.%s", world));
        String time = Utils.Time.timeToString(Utils.Time.parseTime(this.sateEvent.getDelay().getLeftSeconds()));
        String title = Utils.applyReplacements(this.getDefaultTitle(),
                "event-%-" + this.sateEvent.getName(),
                "left-%-" + this.sateEvent.getDelay().getLeftSeconds(),
                "x-%-" + x, "y-%-" + y, "z-%-" + z, "world-%-" + world,
                "ls-world-%-" + ruWorldName,
                "time-%-" + time);

        Player player = this.getPlayers().isEmpty() ? null : this.getPlayers().get(0);
        this.updateTitle(player == null ? title : Utils.setPlaceholders(player, title));

        SateEvent.Delay delay = this.sateEvent.getDelay();
        this.setProgress((float) delay.getLeftSeconds() / delay.getMax());

        return this;
    }

    @Deprecated
    public void remove() {
        this.delete();
    }

    public void addPlayer(Player player, Predicate<Player> predicate) {
        if (predicate == null || predicate.test(player)) this.addPlayer(player);
    }

    public static NamespacedKey key() {
        return new NamespacedKey(SateEvents.getInstance(), "eventbar-" + UUID.randomUUID());
    }
}
