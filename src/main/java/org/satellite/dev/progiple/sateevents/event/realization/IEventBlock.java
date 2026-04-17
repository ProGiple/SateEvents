package org.satellite.dev.progiple.sateevents.event.realization;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.satellite.dev.progiple.sateevents.listeners.events.impl.EventBlockDestroyEvent;
import org.satellite.dev.progiple.sateevents.listeners.events.impl.EventBlockPlaceEvent;

public interface IEventBlock {
    Block getBlock();
    SateEvent getEvent();
    boolean onInteract(PlayerInteractEvent e);
    boolean onBreak(BlockBreakEvent e);
    default boolean place() {
        EventBlockPlaceEvent event = new EventBlockPlaceEvent(getEvent(), getEvent().getStage(), this);
        return event.call();
    }

    default boolean destroy() {
        EventBlockDestroyEvent event = new EventBlockDestroyEvent(getEvent(), getEvent().getStage(), this);
        return event.call();
    }
}
