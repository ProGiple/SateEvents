package org.satellite.dev.progiple.sateevents.event.realization;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventTimer;
import org.satellite.dev.progiple.sateevents.listeners.events.impl.EventBlockDestroyEvent;
import org.satellite.dev.progiple.sateevents.listeners.events.impl.EventBlockPlaceEvent;

import java.util.concurrent.CompletableFuture;

public interface IEventBlock {
    Block getBlock();
    SateEvent getEvent();
    boolean onInteract(PlayerInteractEvent e);
    boolean onBreak(BlockBreakEvent e);
    default boolean place() {
        EventBlockPlaceEvent event = new EventBlockPlaceEvent(getEvent(), getEvent().getStage(), this);
        return event.call();
    }

    default String getConfigPath() {
        Location l = getBlock().getLocation();
        return "blocks." + l.getWorld().getName() + ";" + l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ();
    }

    default boolean saveState() {
        String val = this.getEvent().getSavesConfig().getString(getConfigPath());
        if (val != null) return false;

        val = getBlock().getBlockData().getAsString();
        if (val.isEmpty()) val = Material.AIR.createBlockData().getAsString();

        this.getEvent().getSavesConfig().set(getConfigPath(), val);
        this.getEvent().getSavesConfig().save();
        return true;
    }

    default CompletableFuture<Boolean> saveStateAsync() {
        return CompletableFuture.supplyAsync(this::saveState);
    }

    default void unsaveState() {
        this.getEvent().getSavesConfig().set(getConfigPath(), null);
        this.getEvent().getSavesConfig().save();
    }

    default boolean destroy() {
        unsaveState();
        EventBlockDestroyEvent event = new EventBlockDestroyEvent(getEvent(), getEvent().getStage(), this);
        return event.call();
    }

    default void timerTick(boolean isFinally, EventTimer timer) {
    }
}
