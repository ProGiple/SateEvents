package org.satellite.dev.progiple.sateevents.event.realization.settings.spawn;

import org.bukkit.Location;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.satellite.dev.progiple.sateevents.event.realization.settings.EventSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ISpawnSettings;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StaticSpawnSettings implements ISpawnSettings {
    private final List<Location> locations;
    public StaticSpawnSettings(List<Location> locations) {
        this.locations = locations;
    }

    @Override
    public CompletableFuture<Location> findLocationAsync(EventSettings settings) {
        return CompletableFuture.completedFuture(findLocation(settings));
    }

    @Override
    public Location findLocation(EventSettings settings) {
        return LunaMath.getRandom(locations);
    }
}
