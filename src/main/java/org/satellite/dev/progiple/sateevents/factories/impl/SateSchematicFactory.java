package org.satellite.dev.progiple.sateevents.factories.impl;

import org.satellite.dev.progiple.sateevents.event.realization.settings.ISchematicSettings;
import org.satellite.dev.progiple.sateevents.factories.SchematicSettingsFactory;
import org.satellite.dev.progiple.sateevents.factories.storage.FactoryId;
import org.satellite.dev.progiple.sateevents.event.realization.settings.schematic.SateSchematicsSettings;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.util.List;

@FactoryId("sateschematics")
public class SateSchematicFactory implements SchematicSettingsFactory {
    @Override
    public ISchematicSettings<?, ?> create(List<String> storage) {
        List<YAMLSchematic> schematics = storage.stream().map(SchematicManager::getSchem).toList();
        return new SateSchematicsSettings(schematics);
    }
}
