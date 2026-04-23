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
    private final EventSettings eventSettings;
    private final List<World> allowedWorlds;
    private final CoordinateSettings coordinateSettings;
    private final ListSettings<Material> materialList;
    private final ListSettings<Biome> biomeList;
    private final LocationGen locationGen;

    @Builder
    public RandomSpawnSettings(EventSettings settings,
                               List<World> worlds,
                               CoordinateSettings coordinates,
                               ListSettings<Material> materials,
                               ListSettings<Biome> biomes,
                               LocationGen locationGen) {
        this.eventSettings = settings;
        this.allowedWorlds = worlds;
        this.coordinateSettings = coordinates;
        this.materialList = materials;
        this.biomeList = biomes;
        this.locationGen = locationGen;
    }

    @Override
    public CompletableFuture<Location> findLocationAsync() {
        World world = LunaMath.getRandom(this.allowedWorlds);
        return this.locationGen.findLocationAsync(world, this, eventSettings);
    }

    @Override
    public Location findLocation() {
        World world = LunaMath.getRandom(this.allowedWorlds);
        return this.locationGen.findLocation(world, this, eventSettings);
    }
}
