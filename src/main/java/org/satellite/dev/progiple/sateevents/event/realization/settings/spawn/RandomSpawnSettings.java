package org.satellite.dev.progiple.sateevents.event.realization.settings.spawn;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.satellite.dev.progiple.sateevents.event.realization.searcher.LocationGen;
import org.satellite.dev.progiple.sateevents.event.realization.settings.CoordinateSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.EventSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ListSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ISpawnSettings;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class RandomSpawnSettings implements ISpawnSettings {
    private final List<World> allowedWorlds;
    private final CoordinateSettings coordinateSettings;
    private final ListSettings<Material> materialList;
    private final ListSettings<Biome> biomeList;
    private final LocationGen locationGen;

    @Builder
    public RandomSpawnSettings(List<World> worlds,
                               CoordinateSettings coordinates,
                               ListSettings<Material> materials,
                               ListSettings<Biome> biomes,
                               LocationGen locationGen) {
        this.allowedWorlds = worlds;
        this.coordinateSettings = coordinates;
        this.materialList = materials;
        this.biomeList = biomes;
        this.locationGen = locationGen;
    }

    @Override
    public CompletableFuture<Location> findLocationAsync(EventSettings settings) {
        World world = LunaMath.getRandom(this.allowedWorlds);
        return this.locationGen.findLocationAsync(world, this, settings);
    }

    @Override
    public Location findLocation(EventSettings eventSettings) {
        World world = LunaMath.getRandom(this.allowedWorlds);
        return this.locationGen.findLocation(world, this, eventSettings);
    }
}
