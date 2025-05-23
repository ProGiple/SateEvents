package org.satellite.dev.progiple.sateevents.events;

import lombok.Getter;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.LunaPlugin;

import java.util.List;

@Getter
public abstract class EventManager {
    private final LunaPlugin lunaPlugin;
    private final String name;
    public EventManager(LunaPlugin lunaPlugin, String name) {
        this.lunaPlugin = lunaPlugin;
        this.name = ColorManager.color(name);
    }

    public abstract SateEvent createEvent();
    public abstract void run(SateEvent sateEvent);
    public abstract List<String> getTimes();
}
