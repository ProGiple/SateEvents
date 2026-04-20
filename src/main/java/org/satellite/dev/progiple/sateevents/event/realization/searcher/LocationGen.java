package org.satellite.dev.progiple.sateevents.event.realization.searcher;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.satellite.dev.progiple.sateevents.event.realization.settings.EventSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.spawn.RandomSpawnSettings;

import java.util.concurrent.CompletableFuture;

public interface LocationGen {
    CompletableFuture<Location> findLocationAsync(World world,
                                                  RandomSpawnSettings randomSpawnSettings,
                                                  EventSettings eventSettings);
    Location findLocation(World world, RandomSpawnSettings randomSpawnSettings, EventSettings eventSettings);

    default Location checkLocation(World world, int x, int z, RandomSpawnSettings settings, EventSettings eventSettings) {
        int y = world.getHighestBlockYAt(x, z);
        if (y > settings.getCoordinateSettings().maxY() ||
                y < settings.getCoordinateSettings().minY()) return null;

        Block block = world.getBlockAt(x, y, z);
        if (!settings.getMaterialList().isValid(block.getType())) return null;

        Biome biome = block.getBiome();
        if (!settings.getBiomeList().isValid(biome)) return null;

        if (eventSettings != null) {
            var regions = eventSettings.getRegionSettings();
            if (regions != null && GuardManager.hasRegionsInside(block.getLocation(), regions.size()))
                return null;
        }

        return new Location(world, x, y + 1, z);
    }
}
