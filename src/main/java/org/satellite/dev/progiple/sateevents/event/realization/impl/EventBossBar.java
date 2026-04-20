package org.satellite.dev.progiple.sateevents.event.realization.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.LunaBossBar;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;

import java.util.UUID;

public class EventBossBar extends LunaBossBar {
    protected final SateEvent event;
    public EventBossBar(@NotNull String title,
                        BarColor barColor,
                        BarStyle barStyle,
                        @NotNull SateEvent event) {
        super(title, barColor, barStyle, key());
        this.event = event;
    }

    public EventBossBar(@NotNull ConfigurationSection section, @NotNull SateEvent event) {
        super(section, key());
        this.event = event;
    }

    public EventBossBar(@NotNull String title,
                        BarColor barColor,
                        BarStyle barStyle,
                        @NotNull NamespacedKey namespacedKey,
                        SateEvent sateEvent) {
        super(title, barColor, barStyle, namespacedKey);
        this.event = sateEvent;
    }

    @Override
    public EventBossBar updateTitle(String title) {
        title = getDefaultTitle();

        String[] stageReplacer = event.getStage().bossBarTitleReplacer(title);
        String line = Utils.applyReplacements(title, stageReplacer);

        line = titleParser(line);
        if (!this.getPlayers().isEmpty()) line = Utils.setPlaceholders(this.getPlayers().get(0), line);
        else line = Utils.setPlaceholders(null, line);

        line = ColorManager.color(line);
        super.updateTitle(line);
        return this;
    }

    public boolean canAddPlayer(Player player) {
        return true;
    }

    public String titleParser(String title) {
        return title;
    }

    public EventBossBar addPlayerWithConditions(Player player) {
        if (canAddPlayer(player)) super.addPlayer(player);
        return this;
    }

    @Override
    public EventBossBar setProgress(float value) {
        value = Math.max(0, Math.min(1.0f, (float) event.getRemainsSeconds() / event.getLifeTime()));
        super.setProgress(value);
        return this;
    }

    public static NamespacedKey key() {
        return new NamespacedKey(SateEvents.getInstance(), "eventbar-" + UUID.randomUUID());
    }
}
