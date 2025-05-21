package org.satellite.dev.progiple.sateevents.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor @Getter
public abstract class EventBlock {
    private final Block block;
    private final EventManager eventManager;

    public abstract void onInteract(PlayerInteractEvent e);
    public abstract void place();
    public abstract void destroy();
}
