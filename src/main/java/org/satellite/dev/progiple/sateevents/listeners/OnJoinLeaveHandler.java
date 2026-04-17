package org.satellite.dev.progiple.sateevents.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.satellite.dev.progiple.sateevents.event.SateEventsManager;

public class OnJoinLeaveHandler implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        SateEventsManager.getLaunchedEvents().forEach(event -> {
            if (event.getStage() != null)
                if (event.getStage().getBossBar() != null)
                    event.getStage().getBossBar().removePlayer(player);
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Bukkit.getBossBars().forEachRemaining(b -> {
            if (b.getKey().getKey().startsWith("eventbar-"))
                b.removePlayer(player);
        });

        SateEventsManager.getLaunchedEvents().forEach(event -> {
            if (event.getStage() != null)
                if (event.getStage().getBossBar() != null)
                    event.getStage().getBossBar().addPlayerWithConditions(player);
        });
    }
}
