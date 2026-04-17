package org.satellite.dev.progiple.sateevents.factories.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.satellite.dev.progiple.sateevents.factories.SpawnSettingsFactory;
import org.satellite.dev.progiple.sateevents.factories.storage.FactoryId;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ISpawnSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.spawn.StaticSpawnSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@FactoryId("static")
public class StaticSpawnSettingsFactory implements SpawnSettingsFactory {
    @Override
    public ISpawnSettings create(ConfigurationSection section) {
        var locationSection = section.getConfigurationSection("locations");
        List<Location> list = locationSection == null ?
                new ArrayList<>() :
                locationSection.getKeys(false)
                        .stream()
                        .map(k -> {
                            var locSection = locationSection.getConfigurationSection(k);

                            World world = Bukkit.getWorld(locSection.getString("world", "world"));
                            if (world == null) return null;

                            return new Location(
                                    world,
                                    locSection.getInt("x"),
                                    locSection.getInt("y"),
                                    locSection.getInt("z")
                            );
                        })
                        .filter(Objects::nonNull)
                        .toList();

        return new StaticSpawnSettings(list);
    }
}
