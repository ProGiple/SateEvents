package org.satellite.dev.progiple.sateevents.event.realization.settings.schematic;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.event.realization.settings.EventSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ISchematicSettings;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class SateSchematicsSettings implements ISchematicSettings<YAMLSchematic, @Nullable PastedSchematic> {
    private final List<YAMLSchematic> storage;
    private final Collection<PastedSchematic> pastedSchematics;
    private final EventSettings eventSettings;
    public SateSchematicsSettings(EventSettings settings, List<YAMLSchematic> storage) {
        this.storage = storage;
        this.eventSettings = settings;
        this.pastedSchematics = new ArrayList<>();
    }

    protected File generateSaveFile() {
        return new File(SateEvents.getInstance().getDataFolder(), "saves/" + UUID.randomUUID() + ".yml");
    }

    @Override
    public @Nullable PastedSchematic paste(Location location, YAMLSchematic schematic) {
        if (schematic == null) return null;

        var schem = schematic.paste(location, null);
        if (schem == null) return null;

        pastedSchematics.add(schem);
        SateEvents.getInstance().async(() -> {
            schem.save(YamlConfiguration.loadConfiguration(generateSaveFile()));
        });

        return schem;
    }

    @Override
    public CompletableFuture<@Nullable PastedSchematic> pasteAsync(Location location, YAMLSchematic schematic) {
        if (schematic == null) return CompletableFuture.completedFuture(null);
        var future = schematic.pasteAsync(location, null);

        return future.thenApplyAsync(s -> {
            if (s == null) return null;
            pastedSchematics.add(s);
            s.save(YamlConfiguration.loadConfiguration(generateSaveFile()));
            return s;
        }, r -> Bukkit.getScheduler().runTaskAsynchronously(SateEvents.getInstance(), r));
    }

    @Override
    public void remove(PastedSchematic schematic) {
        schematic.undoAsync();
        pastedSchematics.remove(schematic);
    }
}
