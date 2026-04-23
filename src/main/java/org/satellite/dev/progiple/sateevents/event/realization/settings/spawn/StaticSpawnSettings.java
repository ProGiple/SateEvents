package org.satellite.dev.progiple.sateevents.event.realization.settings.spawn;

import lombok.Getter;
import org.bukkit.Location;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.satellite.dev.progiple.sateevents.event.realization.settings.EventSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ISpawnSettings;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class StaticSpawnSettings implements ISpawnSettings {
    private final List<Location> locations;
    private final EventSettings eventSettings;
    public StaticSpawnSettings(EventSettings settings, List<Location> locations) {
        this.locations = locations;
        this.eventSettings = settings;
    }

    @Override
    public CompletableFuture<Location> findLocationAsync() {
        return CompletableFuture.completedFuture(findLocation());
    }

    @Override
    public Location findLocation() {
        return LunaMath.getRandom(locations);
    }
}
