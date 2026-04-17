package org.satellite.dev.progiple.sateevents.factories.impl;

import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ISchematicSettings;
import org.satellite.dev.progiple.sateevents.factories.SchematicSettingsFactory;
import org.satellite.dev.progiple.sateevents.factories.storage.FactoryId;
import org.satellite.dev.progiple.sateevents.event.realization.settings.schematic.SateSchematicsSettings;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@FactoryId("sateschematics")
public class SateSchematicFactory implements SchematicSettingsFactory {
    @Override
    public ISchematicSettings<?, ?> create(List<String> storage) {
        File dir = new File(SateEvents.getInstance().getDataFolder(), "sateschematics/");

        List<YAMLSchematic> schematics = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".yaml") || f.getName().endsWith(".yml")) {
                    schematics.add(new YAMLSchematic(f));
                }
            }
        }

        return new SateSchematicsSettings(schematics);
    }
}
