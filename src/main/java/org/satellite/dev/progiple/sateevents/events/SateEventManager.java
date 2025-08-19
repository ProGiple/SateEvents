package org.satellite.dev.progiple.sateevents.events;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;

import java.time.LocalTime;
import java.util.*;

@UtilityClass
public class SateEventManager {
    @Getter private final Set<EventManager> registeredManagers = new HashSet<>();
    @Getter private SateEvent launchedEvent;

    public EventManager getManager(LunaPlugin lunaPlugin) {
        return registeredManagers.stream().filter(m -> m.getLunaPlugin().equals(lunaPlugin)).findFirst().orElse(null);
    }

    public boolean run(EventManager eventManager, SateEvent sateEvent) {
        if (launchedEvent != null) return false;

        forceRun(eventManager, sateEvent);
        return true;
    }

    public boolean run(EventManager eventManager) {
        return run(eventManager, eventManager.createEvent());
    }

    public void forceRun(EventManager eventManager, SateEvent sateEvent) {
        remove();
        eventManager.run(sateEvent);
        launchedEvent = sateEvent;
    }

    public void forceRun(EventManager eventManager) {
        forceRun(eventManager, eventManager.createEvent());
    }

    public EventManager getRandom() {
        List<EventManager> eventManagers = new ArrayList<>(registeredManagers);
        return eventManagers.isEmpty() ? null : eventManagers.get(LunaMath.getRandomInt(0, eventManagers.size()));
    }

    public boolean remove() {
        if (launchedEvent == null) return false;

        launchedEvent.remove();
        launchedEvent = null;
        return true;
    }

    public boolean isTime(EventManager eventManager) {
        LocalTime localTime = Utils.Time.getNextTime(eventManager.getTimes());

        LocalTime now = LocalTime.now();
        return localTime.getMinute() == now.getMinute() && localTime.getHour() == now.getHour();
    }

    public LocalTime getNextTime(EventManager eventManager) {
        return Utils.Time.getNextTime(eventManager.getTimes());
    }

    public EventManager getNext() {
        LocalTime now = LocalTime.now();
        return registeredManagers.stream()
                .min(Comparator.comparing(manager -> {
                    LocalTime nearestTime = Utils.Time.getNextTime(manager.getTimes());

                    long diffInSeconds = Math.abs(nearestTime.toSecondOfDay() - now.toSecondOfDay());
                    return Math.min(diffInSeconds, 24 * 60 * 60 - diffInSeconds);
                }))
                .orElse(null);
    }

    public String getLeftTime(EventManager eventManager) {
        return eventManager == null ? null : Utils.Time.getTimeBetween(getNextTime(eventManager));
    }
}
