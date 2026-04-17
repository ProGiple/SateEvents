package org.satellite.dev.progiple.sateevents.event.realization.settings;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ISchematicSettings<H, R> extends Settings {
    List<H> getStorage();
    Map<SateEvent, Collection<R>> getPastedSchematics();
    R paste(SateEvent event, Location location, H schematic);
    CompletableFuture<R> pasteAsync(SateEvent event, Location location, H schematic);
    void remove(R obj);

    default @Nullable H getRandomSchematic() {
        return LunaMath.getRandom(getStorage());
    }

    default R pasteRandom(SateEvent event, Location location) {
        return paste(event, location, getRandomSchematic());
    }

    default CompletableFuture<R> pasteAsyncRandom(SateEvent event, Location location) {
        return pasteAsync(event, location, getRandomSchematic());
    }

    default void removeAll(SateEvent event) {
        new ArrayList<>(this.getPastedSchematics().get(event)).forEach(this::remove);
        this.getPastedSchematics().remove(event);
    }
}
