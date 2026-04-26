package org.satellite.dev.progiple.sateevents.event;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.LunaPAPIExpansion;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.API.util.utilities.localization.Localization;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.sateevents.event.realization.*;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventStopReason;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

@UtilityClass
public class SateEventsManager {
    @Getter
    private final Set<IEventManager> managers = new HashSet<>();

    public IEventManager getManager(String id) {
        return Utils.find(managers, m -> m.getId().equalsIgnoreCase(id)).orElse(null);
    }

    public Stream<IEventManager> getManagers(LunaPlugin lunaPlugin) {
        return managers.stream().filter(m -> m.getPlugin().equals(lunaPlugin));
    }

    public Stream<IEventManager> getActiveManagers() {
        return managers.stream().filter(IEventManager::isActive);
    }

    public Stream<SateEvent> getLaunchedEvents() {
        return managers.stream().map(IEventManager::getLaunched).filter(Objects::nonNull);
    }

    public boolean isActive() {
        return managers.stream().anyMatch(IEventManager::isActive);
    }

    public boolean isActive(SateEvent sateEvent) {
        return managers.stream().anyMatch(m -> m.isActive(sateEvent));
    }

    public void stopAll(EventStopReason reason) {
        managers.forEach(m -> m.stop(reason));
    }

    public IEventBlock getEventBlock(Block block) {
        return SateEventsManager.getLaunchedEvents()
                .filter(se -> se.getStage() != null)
                .flatMap(se -> se.getStage().getBlocks().stream())
                .filter(b -> b.getBlock().equals(block))
                .findFirst()
                .orElse(null);
    }

    public IEventManager getNextManager() {
        var now = LocalDateTime.now();
        return managers
                .stream()
                .min(Comparator.comparingLong(m -> m.secondsToNext(now)))
                .orElse(null);
    }

    public SateEvent getNearestEvent(Location location) {
        List<SateEvent> events = getLaunchedEvents().toList();
        if (events.isEmpty()) return null;

        return events
                .stream()
                .filter(e -> e.getStage() instanceof ILocationStage loc &&
                        loc.getLocation() != null &&
                        loc.getLocation().getWorld().equals(location.getWorld()))
                .min(Comparator.comparingDouble(e -> ((ILocationStage) e.getStage()).getLocation().distance(location)))
                .orElse(LunaMath.getRandom(events));
    }
}
