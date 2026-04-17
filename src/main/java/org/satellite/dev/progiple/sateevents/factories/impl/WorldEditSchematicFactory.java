package org.satellite.dev.progiple.sateevents.factories.impl;

import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ISchematicSettings;
import org.satellite.dev.progiple.sateevents.factories.SchematicSettingsFactory;
import org.satellite.dev.progiple.sateevents.factories.storage.FactoryId;
import org.satellite.dev.progiple.sateevents.event.realization.settings.schematic.WorldEditSchemSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@FactoryId("worldedit")
public class WorldEditSchematicFactory implements SchematicSettingsFactory {
    @Override
    public ISchematicSettings<?, ?> create(List<String> storage) {
        File dir = new File(SateEvents.getInstance().getDataFolder(), "worldedit/");

        List<File> files = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".schem") || f.getName().endsWith(".schematic")) {
                    files.add(f);
                }
            }
        }

        return new WorldEditSchemSettings(files);
    }
}
