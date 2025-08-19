package org.satellite.dev.progiple.sateevents;

import org.bukkit.Location;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

public class SSchem {
    private PastedSchematic schematic;

    public void place(String id, Location location) {
        YAMLSchematic yamlSchematic = SchematicManager.getSchem(id);
        if (yamlSchematic == null || this.schematic != null) return;

        this.schematic = yamlSchematic.paste(location, null);
    }

    public void undo() {
        if (this.schematic != null) this.schematic.undo();
    }
}
