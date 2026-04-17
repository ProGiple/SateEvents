package org.satellite.dev.progiple.sateevents.listeners.events.impl;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.sateevents.event.realization.IEventBlock;
import org.satellite.dev.progiple.sateevents.event.realization.IEventStage;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;
import org.satellite.dev.progiple.sateevents.listeners.events.EventBlockEvent;

public class EventBlockPlaceEvent extends EventBlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    public EventBlockPlaceEvent(SateEvent event, IEventStage stage, IEventBlock block) {
        super(event, stage, block);
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
