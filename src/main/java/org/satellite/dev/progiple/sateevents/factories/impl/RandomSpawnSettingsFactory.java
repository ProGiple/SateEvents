package org.satellite.dev.progiple.sateevents.factories.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateevents.event.realization.settings.*;
import org.satellite.dev.progiple.sateevents.factories.LocationGenFactory;
import org.satellite.dev.progiple.sateevents.factories.SpawnSettingsFactory;
import org.satellite.dev.progiple.sateevents.factories.storage.Factories;
import org.satellite.dev.progiple.sateevents.factories.storage.FactoryId;
import org.satellite.dev.progiple.sateevents.event.realization.settings.spawn.RandomSpawnSettings;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@FactoryId("random")
public class RandomSpawnSettingsFactory implements SpawnSettingsFactory {
    @Override
    public ISpawnSettings create(EventSettings settings, ConfigurationSection section) {
        List<World> worlds = section.getStringList("worlds")
                .stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .toList();

        var coordSection = section.getConfigurationSection("coordinates");
        var coords = coordSection == null ?
                new CoordinateSettings(0, 1000, 0, 255, 0, 1000) :
                new CoordinateSettings(
                        coordSection.getInt("minX"),
                        coordSection.getInt("maxX"),
                        coordSection.getInt("minY"),
                        coordSection.getInt("maxY"),
                        coordSection.getInt("minZ"),
                        coordSection.getInt("maxZ")
                );

        var listSection = section.getConfigurationSection("blockMaterials");
        ListSettings<Material> materialList = generateListSettings(
                listSection,
                Material::matchMaterial);

        listSection = section.getConfigurationSection("biomes");
        ListSettings<Biome> biomeList = generateListSettings(
                listSection,
                s -> Utils.getEnumValue(Biome.class, s.toUpperCase()));

        String genId = section.getString("locationGenId", "gen1");
        return new RandomSpawnSettings(settings, worlds, coords, materialList, biomeList,
                Factories.getFactory(LocationGenFactory.class, genId).create());
    }

    private static <E> ListSettings<E> generateListSettings(ConfigurationSection section, Function<String, E> map) {
        if (section == null) return new ListSettings<>(Set.of(), FilterType.BLACKLIST);

        List<String> list = section.getStringList("list");
        FilterType filterType = Utils.getEnumValue(
                FilterType.class,
                section.getString("filter"),
                FilterType.BLACKLIST);
        return new ListSettings<>(list
                .stream()
                .map(map)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()), filterType);
    }
}
