package org.satellite.dev.progiple.sateevents.event;

import org.bukkit.OfflinePlayer;
import org.novasparkle.lunaspring.LunaPlugin;

@FunctionalInterface
public interface EventPlaceholderRequest {
    String sendRequest(OfflinePlayer player, String params);

    default void registerPlaceholder(String identifier, LunaPlugin plugin) {
        plugin.createPlaceholder(identifier, this::sendRequest);
    }

    default void registerPlaceholder(LunaPlugin plugin) {
        plugin.createPlaceholder(this::sendRequest);
    }
}
