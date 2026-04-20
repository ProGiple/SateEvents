package org.satellite.dev.progiple.sateevents.listeners.events;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;

@Getter
public abstract class SateEventEvent extends Event {
    private final SateEvent event;
    public SateEventEvent(SateEvent event) {
        this.event = event;
    }

    public SateEventEvent(SateEvent event, boolean async) {
        super(async);
        this.event = event;
    }

    public boolean call() {
        Bukkit.getServer().getPluginManager().callEvent(this);
        return !(this instanceof Cancellable c) || !c.isCancelled();
    }
}
