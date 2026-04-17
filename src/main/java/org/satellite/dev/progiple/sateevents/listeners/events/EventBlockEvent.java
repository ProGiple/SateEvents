package org.satellite.dev.progiple.sateevents.listeners.events;

import lombok.Getter;
import org.satellite.dev.progiple.sateevents.event.realization.IEventBlock;
import org.satellite.dev.progiple.sateevents.event.realization.IEventStage;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;

@Getter
public abstract class EventBlockEvent extends SateStagedEvent {
    private final IEventBlock block;
    public EventBlockEvent(SateEvent event, IEventStage stage, IEventBlock block) {
        super(event, stage);
        this.block = block;
    }
}
