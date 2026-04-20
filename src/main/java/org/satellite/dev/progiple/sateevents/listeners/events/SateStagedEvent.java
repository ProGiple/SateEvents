package org.satellite.dev.progiple.sateevents.listeners.events;

import lombok.Getter;
import lombok.Setter;
import org.satellite.dev.progiple.sateevents.event.realization.IEventStage;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;

@Getter @Setter
public abstract class SateStagedEvent extends SateEventEvent {
    protected IEventStage stage;
    public SateStagedEvent(SateEvent event, IEventStage stage) {
        super(event);
        this.stage = stage;
    }

    public SateStagedEvent(SateEvent event, IEventStage stage, boolean async) {
        super(event, async);
        this.stage = stage;
    }
}
