package org.satellite.dev.progiple.sateevents.listeners.events.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventRequest;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventStopReason;
import org.satellite.dev.progiple.sateevents.listeners.events.SateEventEvent;

@Getter @Setter
public class SateEventStopEvent extends SateEventEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final EventStopReason reason;
    private EventRequest request;
    public SateEventStopEvent(SateEvent event, EventStopReason reason, EventRequest eventRequest) {
        super(event);
        this.reason = reason;
        this.request = eventRequest;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
