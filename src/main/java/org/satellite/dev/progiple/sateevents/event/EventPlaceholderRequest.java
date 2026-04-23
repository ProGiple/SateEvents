package org.satellite.dev.progiple.sateevents.event;

import org.bukkit.OfflinePlayer;
import org.novasparkle.lunaspring.LunaPlugin;

@FunctionalInterface
public interface EventPlaceholderRequest {
    String sendRequest(OfflinePlayer player, String argument, String params);

    default void registerPlaceholder(String identifier, LunaPlugin plugin) {
        plugin.createPlaceholder(identifier, (o, p) -> sendRequest(o, identifier, p));
    }

    default void registerPlaceholder(LunaPlugin plugin) {
        registerPlaceholder(plugin.getName().toLowerCase(), plugin);
    }
}
