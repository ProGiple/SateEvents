package org.satellite.dev.progiple.sateevents;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

public class SSchem {
    private final Configuration config = new Configuration(SateEvents.getINSTANCE().getDataFolder(), "sync.yml");
    private PastedSchematic schematic;

    public void place(String id, Location location) {
        YAMLSchematic yamlSchematic = SchematicManager.getSchem(id);
        if (yamlSchematic == null || this.schematic != null) return;

        this.schematic = yamlSchematic.paste(location, null);

        ConfigurationSection section = config.createSection((String) null, "schem");
        this.schematic.save(section);
        this.config.save();
    }

    public void undo() {
        if (this.schematic != null) {
            this.schematic.undo();
            this.remove();
        }
    }

    public void remove() {
        this.config.set("schem", null);
        this.config.save();
    }

    public void safeRemove() {
        ConfigurationSection section = this.config.getSection("schem");
        if (section == null) return;

        this.schematic = new PastedSchematic(section);
        this.undo();
    }
}
