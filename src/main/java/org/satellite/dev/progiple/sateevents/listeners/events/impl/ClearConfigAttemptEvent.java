package org.satellite.dev.progiple.sateevents.listeners.events.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@RequiredArgsConstructor @Getter
public class ClearConfigAttemptEvent extends Event {
    public static final HandlerList HANDLERS = new HandlerList();

    private final File file;
    private final YamlConfiguration configuration;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
