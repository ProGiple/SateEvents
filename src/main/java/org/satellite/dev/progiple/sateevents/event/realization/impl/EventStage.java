package org.satellite.dev.progiple.sateevents.event.realization.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.satellite.dev.progiple.sateevents.event.realization.IEventBlock;
import org.satellite.dev.progiple.sateevents.event.realization.IEventStage;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;

import java.util.ArrayList;
import java.util.Collection;

@Getter @Setter
public abstract class EventStage implements IEventStage {
    private final Collection<IEventBlock> blocks = new ArrayList<>();
    private final String id;
    private final String name;
    private final EventTimer timer;
    private final short index;
    private EventBossBar bossBar;

    public EventStage(short index, String id, String name, SateEvent event, int lifeTime) {
        this.index = index;
        this.id = id;
        this.name = name;
        this.timer = new EventTimer(event, lifeTime);
    }

    public EventStage(short index, ConfigurationSection section, SateEvent event, int lifeTime) {
        this.index = index;
        this.id = section.getName();
        this.name = section.getString("name");
        this.timer = new EventTimer(event, lifeTime);
    }

    public EventStage(short index, String id, String name, EventTimer timer) {
        this.index = index;
        this.id = id;
        this.name = name;
        this.timer = timer;
    }

    public EventStage(short index, ConfigurationSection section, EventTimer timer) {
        this.index = index;
        this.id = section.getName();
        this.name = section.getString("name");
        this.timer = timer;
    }

    @Override
    public SateEvent getEvent() {
        return this.timer.getEvent();
    }

    public EventBossBar createBossBar(ConfigurationSection section) {
        var bar = new EventBossBar(section, getEvent());
        this.bossBar = bar;
        return bar;
    }

    @Override
    public EventRequest start(@Nullable String[] args) {
        showBossBar();
        startTimer();
        return EventRequest.SUCCESS;
    }

    @Override
    public EventRequest stop(EventStopReason reason) {
        cancelTimer();
        deleteBossBar();
        return EventRequest.SUCCESS;
    }
}
