package org.satellite.dev.progiple.sateevents.event.realization.searcher.gens;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.realization.searcher.LocationGen;
import org.satellite.dev.progiple.sateevents.event.realization.settings.EventSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.spawn.RandomSpawnSettings;

import java.util.concurrent.CompletableFuture;

public class LocationGen2 implements LocationGen {
    @Override
    public CompletableFuture<Location> findLocationAsync(World world, RandomSpawnSettings settings, EventSettings eventSettings) {
        return CompletableFuture.supplyAsync(
                () -> this.findLocation(world, settings, eventSettings),
                r -> Bukkit.getScheduler().runTaskAsynchronously(SateEvents.getInstance(), r));
    }

    @Override
    public Location findLocation(World world, RandomSpawnSettings settings, EventSettings eventSettings) {
        if (world == null) return null;

        int minX = settings.getCoordinateSettings().minX();
        int maxX = settings.getCoordinateSettings().maxX();
        int minZ = settings.getCoordinateSettings().minZ();
        int maxZ = settings.getCoordinateSettings().maxZ();

        int width = maxX - minX;
        int depth = maxZ - minZ;

        int step = Math.max(1, Math.min(width, depth) / 20);

        int attempts = Config.getFindLocAtt();
        int checked = 0;
        while (checked < attempts) {
            for (int i = 0; i < step && checked < attempts; i++) {
                for (int j = 0; j < step && checked < attempts; j++) {
                    int x = minX + LunaMath.getRandomInt(0, width);
                    int z = minZ + LunaMath.getRandomInt(0, depth);

                    checked++;
                    Location loc = checkLocation(world, x, z, settings, eventSettings);
                    if (loc != null) return loc;
                }
            }

            step = Math.max(1, step / 2);
        }

        return null;
    }
}