package org.satellite.dev.progiple.sateevents.factories;

import org.bukkit.configuration.ConfigurationSection;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ISpawnSettings;

public interface SpawnSettingsFactory extends Factory {
    ISpawnSettings create(ConfigurationSection section);
}
