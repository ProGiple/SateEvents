package org.satellite.dev.progiple.sateevents.listeners.events.impl;

import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.sateevents.event.realization.IEventStage;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;
import org.satellite.dev.progiple.sateevents.listeners.events.SateEventEvent;
import org.satellite.dev.progiple.sateevents.listeners.events.SateStagedEvent;

@Getter
public class SateEventStartEvent extends SateStagedEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    public SateEventStartEvent(SateEvent event, IEventStage nextStage) {
        super(event, nextStage);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
