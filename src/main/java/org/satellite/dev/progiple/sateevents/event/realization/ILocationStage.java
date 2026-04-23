package org.satellite.dev.progiple.sateevents.event.realization;

import org.bukkit.Location;

import java.util.function.Function;

public interface ILocationStage extends IEventStage {
    Location getLocation();
    void setLocation(Location location);
}
