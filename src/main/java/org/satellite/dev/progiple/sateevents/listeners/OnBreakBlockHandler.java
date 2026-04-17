package org.satellite.dev.progiple.sateevents.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.satellite.dev.progiple.sateevents.event.SateEventsManager;

public class OnBreakBlockHandler implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (block.getType().isAir()) return;

        var eventBlock = SateEventsManager.getEventBlock(block);
        if (eventBlock != null && eventBlock.onBreak(e)) e.setCancelled(true);
    }
}
