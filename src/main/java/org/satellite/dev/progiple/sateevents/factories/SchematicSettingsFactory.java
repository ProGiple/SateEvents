package org.satellite.dev.progiple.sateevents.factories;

import org.satellite.dev.progiple.sateevents.event.realization.settings.ISchematicSettings;

import java.util.List;

public interface SchematicSettingsFactory extends Factory {
    ISchematicSettings<?, ?> create(List<String> storage);
}
