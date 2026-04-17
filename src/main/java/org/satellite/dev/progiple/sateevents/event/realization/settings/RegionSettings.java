package org.satellite.dev.progiple.sateevents.event.realization.settings;

import com.sk89q.worldguard.protection.flags.Flag;

import java.util.Map;

public record RegionSettings(int size, Map<Flag<?>, Object> flags) implements Settings {
}
