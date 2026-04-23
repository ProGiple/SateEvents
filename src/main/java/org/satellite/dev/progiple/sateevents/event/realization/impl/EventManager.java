package org.satellite.dev.progiple.sateevents.event.realization.impl;

import lombok.Getter;
import lombok.Setter;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.sateevents.timeParsers.TimeParser;
import org.satellite.dev.progiple.sateevents.event.EventTime;
import org.satellite.dev.progiple.sateevents.event.realization.IEventManager;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;

import java.util.*;

@Getter
public abstract class EventManager implements IEventManager {
    private final LunaPlugin plugin;
    private final Collection<EventTime> spawnTimes;

    @Setter
    private SateEvent launched;
    protected TimeParser timeParser;

    public EventManager(LunaPlugin plugin) {
        this.plugin = plugin;
        this.spawnTimes = new ArrayList<>();
        this.timeParser = new TimeParser();
    }

    public EventManager(LunaPlugin plugin, TimeParser timeParser) {
        this.plugin = plugin;
        this.timeParser = timeParser == null ? new TimeParser() : timeParser;
        this.spawnTimes = new ArrayList<>();
    }

    public EventManager(LunaPlugin plugin, TimeParser timeParser, Collection<EventTime> spawnTimes) {
        this.plugin = plugin;
        this.timeParser = timeParser == null ? new TimeParser() : timeParser;
        this.spawnTimes = spawnTimes;
    }
}
