package org.satellite.dev.progiple.sateevents.event.realization.searcher.gens;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.realization.searcher.LocationGen;
import org.satellite.dev.progiple.sateevents.event.realization.settings.EventSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.spawn.RandomSpawnSettings;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LocationGen1 implements LocationGen {
    @Override
    public CompletableFuture<Location> findLocationAsync(World world, RandomSpawnSettings settings, EventSettings eventSettings) {
        return CompletableFuture.supplyAsync(() -> this.findLocation(world, settings, eventSettings));
    }

    @Override
    public Location findLocation(World world, RandomSpawnSettings settings, EventSettings eventSettings) {
        if (world == null) return null;

        Location location = null;

        int attempts = Config.getFindLocAtt();
        while (attempts-- > 0) {
            int x = LunaMath.getRandomInt(
                    settings.getCoordinateSettings().minX(),
                    settings.getCoordinateSettings().maxX());
            int z = LunaMath.getRandomInt(
                    settings.getCoordinateSettings().minZ(),
                    settings.getCoordinateSettings().maxZ());

            location = checkLocation(world, x, z, settings, eventSettings);
            if (location != null) break;
        }

        return location;
    }
}
