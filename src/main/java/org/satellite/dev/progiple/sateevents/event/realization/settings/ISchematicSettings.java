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
    EventSettings getEventSettings();
    List<H> getStorage();
    Collection<R> getPastedSchematics();
    R paste(Location location, H schematic);
    CompletableFuture<R> pasteAsync(Location location, H schematic);
    void remove(R obj);

    default @Nullable H getRandomSchematic() {
        return LunaMath.getRandom(getStorage());
    }

    default R pasteRandom(Location location) {
        return paste(location, getRandomSchematic());
    }

    default CompletableFuture<R> pasteAsyncRandom(Location location) {
        return pasteAsync(location, getRandomSchematic());
    }

    default void removeAll() {
        new ArrayList<>(getPastedSchematics()).forEach(this::remove);
        this.getPastedSchematics().clear();
    }
}
