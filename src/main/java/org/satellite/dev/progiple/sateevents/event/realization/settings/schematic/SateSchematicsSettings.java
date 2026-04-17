package org.satellite.dev.progiple.sateevents.event.realization.settings.schematic;

import lombok.Getter;
import org.bukkit.Location;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ISchematicSettings;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;

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
        var schem = schematic.paste(location, null);

        var list = pastedSchematics.computeIfAbsent(event, k -> new ArrayList<>());
        list.add(schem);

        return schem;
    }

    @Override
    public CompletableFuture<PastedSchematic> pasteAsync(SateEvent event, Location location, YAMLSchematic schematic) {
        return schematic.pasteAsync(location, null).thenApply(s -> {
            var list = pastedSchematics.computeIfAbsent(event, k -> new ArrayList<>());
            list.add(s);
            return s;
        });
    }

    @Override
    public void remove(PastedSchematic schematic) {
        schematic.undoAsync();
        pastedSchematics.forEach((k, v) -> v.remove(schematic));
    }
}
