package org.satellite.dev.progiple.sateevents.listeners.events.impl;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.sateevents.event.realization.IEventStage;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;
import org.satellite.dev.progiple.sateevents.listeners.events.SateStagedEvent;

@Getter
public class AsyncSateEventTickEvent extends SateStagedEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final int tickedTime;
    private final int lifeTime;
    private final boolean isFinally;
    public AsyncSateEventTickEvent(SateEvent event,
                                   IEventStage stage,
                                   int ticked,
                                   int lifeTime,
                                   boolean isFinally) {
        super(event, stage);
        this.tickedTime = ticked;
        this.lifeTime = lifeTime;
        this.isFinally = isFinally;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
