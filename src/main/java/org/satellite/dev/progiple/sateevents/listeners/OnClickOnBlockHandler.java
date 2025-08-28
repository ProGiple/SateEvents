package org.satellite.dev.progiple.sateevents.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.satellite.dev.progiple.sateevents.events.EventBlock;
import org.satellite.dev.progiple.sateevents.events.SateEvent;
import org.satellite.dev.progiple.sateevents.events.SateEventManager;

import java.util.UUID;

public class OnClickOnBlockHandler implements Listener {
    private final CooldownPrevent<UUID> uuidCooldownPrevent = new CooldownPrevent<>(75);

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || block.getType().isAir()) return;

        SateEvent sateEvent = SateEventManager.getLaunchedEvent();
        if (sateEvent == null) return;

        EventBlock eventBlock = sateEvent.getEventBlocks()
                .stream()
                .filter(b -> b.getBlock().equals(block))
                .findFirst()
                .orElse(null);
        if (eventBlock != null) {
            if (uuidCooldownPrevent.isCancelled(e, e.getPlayer().getUniqueId())) e.setCancelled(true);
            else eventBlock.onInteract(e);
        }
    }
}
