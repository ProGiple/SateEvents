package org.satellite.dev.progiple.sateevents.event.realization.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.novasparkle.lunaspring.API.util.utilities.tasks.LunaTask;
import org.satellite.dev.progiple.sateevents.event.SateEventTimer;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;
import org.satellite.dev.progiple.sateevents.listeners.events.impl.AsyncSateEventTickEvent;

@Getter @Setter
public class EventTimer extends LunaTask implements SateEventTimer {
    private final SateEvent event;
    private int lifeTime;
    private int tickTimes = 0;
    public EventTimer(SateEvent event, int lifeTime) {
        this.event = event;
        this.lifeTime = lifeTime;
    }

    public EventTimer(SateEvent event) {
        this.event = event;
        this.lifeTime = 0;
    }

    @Override @SneakyThrows
    @SuppressWarnings("all")
    public void start() {
        while (tickTimes < lifeTime) {
            if (!this.isActive()) return;
            tickEvent(false);
            if (event.getStage().getBossBar() != null) event.getStage().getBossBar().update();
            event.timerTick(false, this);
            Thread.sleep(1000L);
            tickTimes++;
        }

        event.timerTick(true, this);
        tickEvent(true);
        if (event.nextStage() == EventRequest.STAGE_POOL_IS_EMPTY)
            Bukkit.getScheduler().runTask(event.getManager().getPlugin(), () ->
                    event.stop(EventStopReason.STAGE_POOL_IS_EMPTY));
    }

    private void tickEvent(boolean f) {
        var event = new AsyncSateEventTickEvent(
                this.event,
                this.event.getStage(),
                tickTimes,
                lifeTime,
                f);
        event.call();
    }
}
