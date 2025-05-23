package org.satellite.dev.progiple.sateevents;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.satellite.dev.progiple.sateevents.events.SateEvent;
import org.satellite.dev.progiple.sateevents.events.SateEventManager;

public class OnClickOnBlockHandler implements Listener {
    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || block.getType().isAir()) return;

        SateEvent sateEvent = SateEventManager.getLaunchedEvent();
        if (sateEvent == null) return;

        sateEvent.getEventBlocks()
                .stream()
                .filter(b -> b.getBlock().equals(block))
                .findFirst()
                .ifPresent(eventBlock -> eventBlock.onInteract(e));
    }
}
