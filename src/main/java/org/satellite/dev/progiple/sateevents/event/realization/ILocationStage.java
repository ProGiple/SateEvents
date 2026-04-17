package org.satellite.dev.progiple.sateevents.event.realization;

import org.bukkit.Location;

import java.util.function.Function;

public interface ILocationStage extends IEventStage {
    Location getLocation();
    void setLocation(Location location);
    Location initializeLocation();

    @Override
    default String[] bossBarTitleReplacer(String title) {
        var location = this.getLocation();
        return new String[]{
                "x-%-" + notnullString(location, Location::getBlockX),
                "y-%-" + notnullString(location, Location::getBlockY),
                "z-%-" + notnullString(location, Location::getBlockZ),
                "world-%-" + notnullString(location, l -> l.getWorld().getName())
        };
    }

    static <E> String notnullString(E parentObject, Function<E, Object> getter) {
        return parentObject == null ? "---" : getter.apply(parentObject).toString();
    }
}
