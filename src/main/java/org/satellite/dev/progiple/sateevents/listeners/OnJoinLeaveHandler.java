package org.satellite.dev.progiple.sateevents.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.satellite.dev.progiple.sateevents.events.EventBar;
import org.satellite.dev.progiple.sateevents.events.SateEvent;
import org.satellite.dev.progiple.sateevents.events.SateEventManager;

public class OnJoinLeaveHandler implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        SateEvent sateEvent = SateEventManager.getLaunchedEvent();
        if (sateEvent == null) return;

        EventBar bar = sateEvent.getEventBar();
        if (bar != null) bar.getBar().removePlayer(e.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Bukkit.getBossBars().forEachRemaining(b -> b.removePlayer(player));

        SateEvent sateEvent = SateEventManager.getLaunchedEvent();
        if (sateEvent == null) return;

        EventBar bar = sateEvent.getEventBar();
        if (bar != null) bar.addPlayer(player, null);
    }
}
