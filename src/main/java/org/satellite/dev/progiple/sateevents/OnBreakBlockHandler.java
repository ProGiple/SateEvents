package org.satellite.dev.progiple.sateevents;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.satellite.dev.progiple.sateevents.events.SateEvent;
import org.satellite.dev.progiple.sateevents.events.SateEventManager;

public class OnBreakBlockHandler implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (block.getType().isAir()) return;

        SateEvent sateEvent = SateEventManager.getLaunchedEvent();
        if (sateEvent == null) return;

        sateEvent.getEventBlocks()
                .stream()
                .filter(b -> b.getBlock().equals(block))
                .findFirst()
                .ifPresent(eventBlock -> e.setCancelled(true));
    }
}
