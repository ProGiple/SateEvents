package org.satellite.dev.progiple.sateevents.event;

import lombok.SneakyThrows;
import org.novasparkle.lunaspring.API.util.utilities.tasks.LunaTask;
import org.satellite.dev.progiple.sateevents.event.realization.IEventManager;

import java.time.LocalDateTime;

public class EventStartScheduler extends LunaTask implements SateEventTimer {
    @Override @SuppressWarnings("all")
    @SneakyThrows
    public void start() {
        while (this.isActive()) {
            Thread.sleep(1000L);

            LocalDateTime now = LocalDateTime.now();
            for (IEventManager manager : SateEventsManager.getManagers()) {
                if (manager.canStartNow(now))
                    manager.startEvent();
            }
        }
    }
}
