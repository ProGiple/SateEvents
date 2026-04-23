package org.satellite.dev.progiple.sateevents.event.realization.settings;

import org.bukkit.Location;

import java.util.concurrent.CompletableFuture;

public interface ISpawnSettings extends Settings {
    EventSettings getEventSettings();
    CompletableFuture<Location> findLocationAsync();
    Location findLocation();
}
