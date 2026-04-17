package org.satellite.dev.progiple.sateevents.listeners.events.impl;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.sateevents.event.realization.IEventStage;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;
import org.satellite.dev.progiple.sateevents.listeners.events.SateStagedEvent;

public class NextStageEvent extends SateStagedEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;
    public NextStageEvent(SateEvent event, IEventStage stage) {
        super(event, stage);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
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
