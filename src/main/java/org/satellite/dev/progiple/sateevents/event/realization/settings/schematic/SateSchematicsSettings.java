package org.satellite.dev.progiple.sateevents.event.realization.settings.schematic;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ISchematicSettings;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class SateSchematicsSettings implements ISchematicSettings<YAMLSchematic, PastedSchematic> {
    private final List<YAMLSchematic> storage;
    private final Map<SateEvent, Collection<PastedSchematic>> pastedSchematics;
    public SateSchematicsSettings(List<YAMLSchematic> storage) {
        this.storage = storage;
        this.pastedSchematics = new HashMap<>();
    }

    @Override
    public PastedSchematic paste(SateEvent event, Location location, YAMLSchematic schematic) {
        if (schematic == null) return null;
        var schem = schematic.paste(location, null);

        var list = pastedSchematics.computeIfAbsent(event, k -> new ArrayList<>());
        list.add(schem);

        File file = new File(SateEvents.getInstance().getDataFolder(), "saves/" + UUID.randomUUID() + ".yml");
        SateEvents.getInstance().async(() -> schem.save(YamlConfiguration.loadConfiguration(file)));

        return schem;
    }

    @Override
    public CompletableFuture<PastedSchematic> pasteAsync(SateEvent event, Location location, YAMLSchematic schematic) {
        return CompletableFuture.supplyAsync(() -> paste(event, location, schematic));
    }

    @Override
    public void remove(PastedSchematic schematic) {
        schematic.undoAsync();
        pastedSchematics.forEach((k, v) -> v.remove(schematic));
    }
}
