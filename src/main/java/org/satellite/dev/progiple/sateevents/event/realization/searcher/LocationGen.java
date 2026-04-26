package org.satellite.dev.progiple.sateevents.event.realization.searcher;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.satellite.dev.progiple.sateevents.EventUtils;
import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.event.realization.settings.EventSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.spawn.RandomSpawnSettings;

import java.util.concurrent.CompletableFuture;

public interface LocationGen {
    default CompletableFuture<Location> findLocationAsync(World world, RandomSpawnSettings settings, EventSettings eventSettings) {
        return CompletableFuture.supplyAsync(
                () -> this.findLocation(world, settings, eventSettings),
                r -> Bukkit.getScheduler().runTaskAsynchronously(SateEvents.getInstance(), r));
    }
    Location findLocation(World world, RandomSpawnSettings randomSpawnSettings, EventSettings eventSettings);

    default Location checkLocation(World world,
                                   int x, int z,
                                   RandomSpawnSettings settings,
                                   EventSettings eventSettings) {
        int y;
        if (world.isChunkLoaded(x >> 4, z >> 4)) {
            y = world.getHighestBlockYAt(x, z);
        }
        else {
            y = EventUtils.getHighestNonAirY(world, x, z);
        }

        if (y > settings.getCoordinateSettings().maxY() || y < settings.getCoordinateSettings().minY()) return null;

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
