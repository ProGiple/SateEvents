package org.satellite.dev.progiple.sateevents.event.realization.settings;

import com.sk89q.worldguard.protection.flags.Flag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.satellite.dev.progiple.sateevents.factories.SchematicSettingsFactory;
import org.satellite.dev.progiple.sateevents.factories.SpawnSettingsFactory;
import org.satellite.dev.progiple.sateevents.factories.storage.Factories;
import org.satellite.dev.progiple.sateevents.event.realization.searcher.gens.LocationGen1;
import org.satellite.dev.progiple.sateevents.event.realization.settings.spawn.RandomSpawnSettings;

import java.util.*;

@Getter @Setter
public class EventSettings implements Settings {
    private String id;
    private String name;
    private RegionSettings regionSettings;
    private ISpawnSettings spawnSettings;
    private ISchematicSettings<?, ?> schematicSettings;
    public EventSettings(ConfigurationSection section) {
        this.name = section.getString("name");
        this.id = section.getString("id");

        ConfigurationSection region = section.getConfigurationSection("region");
        if (region == null) {
            this.regionSettings = new RegionSettings(0, null);
        }
        else {
            Map<Flag<?>, Object> flags;

            ConfigurationSection flagSection = region.getConfigurationSection("flags");
            if (flagSection != null && GuardManager.flags() != null) {
                flags = new HashMap<>();

                var registry = GuardManager.flags().getRegistry();
                flagSection.getKeys(false).forEach(key -> {
                    var flag = registry.get(key);
                    if (flag != null) flags.put(flag, flagSection.get(key));
                });
            }
            else {
                flags = null;
            }

            this.regionSettings = new RegionSettings(region.getInt("size"), flags);
        }

        this.spawnSettings = spawnSettingsFactory(section.getConfigurationSection("spawnSettings"));

        ConfigurationSection schematic = section.getConfigurationSection("schematic");
        if (schematic != null && schematic.getBoolean("enabled", true)) {
            String type = schematic.getString("type", "sateschematics");

            List<String> lines = schematic.getStringList("schems");
            this.schematicSettings = Factories.getFactory(SchematicSettingsFactory.class, type).create(lines);
        }
        else {
            this.schematicSettings = null;
        }
    }

    @Builder
    public EventSettings(String id,
                         String name,
                         RegionSettings regionSettings,
                         ISpawnSettings spawnSettings,
                         ISchematicSettings<?, ?> schematicSettings) {
        this.id = id;
        this.name = name;
        this.regionSettings = regionSettings;
        this.spawnSettings = spawnSettings;
        this.schematicSettings = schematicSettings;
    }

    protected ISpawnSettings spawnSettingsFactory(ConfigurationSection section) {
        if (section == null) {
            WorldBorder border = Bukkit.getWorlds().get(0).getWorldBorder();
            return new RandomSpawnSettings(
                    Bukkit.getWorlds(),
                    new CoordinateSettings(
                            border.getCenter().getBlockX(),
                            (int) (border.getSize() / 2),
                            0,
                            255,
                            border.getCenter().getBlockZ(),
                            (int) (border.getSize() / 2)
                    ),
                    new ListSettings<>(List.of(), FilterType.BLACKLIST),
                    new ListSettings<>(List.of(), FilterType.BLACKLIST),
                    new LocationGen1()
            );
        }

        String spawnType = section.getString("type", "random");
        return Factories.getFactory(SpawnSettingsFactory.class, spawnType).create(section);
    }
}
