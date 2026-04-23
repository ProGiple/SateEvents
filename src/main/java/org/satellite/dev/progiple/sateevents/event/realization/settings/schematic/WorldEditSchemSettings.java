package org.satellite.dev.progiple.sateevents.event.realization.settings.schematic;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.event.realization.settings.EventSettings;
import org.satellite.dev.progiple.sateevents.event.realization.settings.ISchematicSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class WorldEditSchemSettings implements ISchematicSettings<File, Operation> {
    private final List<File> storage;
    private final Collection<Operation> pastedSchematics;
    private final EventSettings eventSettings;
    public WorldEditSchemSettings(EventSettings settings, List<File> files) {
        this.storage = files;
        this.eventSettings = settings;
        this.pastedSchematics = new ArrayList<>();
    }

    @Override
    public Operation paste(Location location, File file) {
        var session = pasteClipboard(location, loadClipboard(file));
        pastedSchematics.add(session);
        return session;
    }

    @Override
    public CompletableFuture<Operation> pasteAsync(Location location, File file) {
        return CompletableFuture.supplyAsync(() -> loadClipboard(file)).thenCompose(clipboard -> {
            CompletableFuture<Operation> future = new CompletableFuture<>();

            Bukkit.getServer().getScheduler().runTask(SateEvents.getInstance(), () -> {
                try {
                    var editSession = pasteClipboard(location, clipboard);
                    pastedSchematics.add(editSession);
                    future.complete(editSession);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });

            return future;
        });
    }

    @Override
    public void remove(Operation obj) {
        obj.cancel();
        pastedSchematics.remove(obj);
    }

    private Clipboard loadClipboard(File file) {
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) return null;

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Operation pasteClipboard(Location location, Clipboard clipboard) {
        if (clipboard == null) return null;

        World bukkitWorld = location.getWorld();
        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(bukkitWorld);

        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .ignoreAirBlocks(true)
                    .build();

            Operations.complete(operation);
            return operation;
        } catch (WorldEditException e) {
            e.printStackTrace();
            return null;
        }
    }
}
