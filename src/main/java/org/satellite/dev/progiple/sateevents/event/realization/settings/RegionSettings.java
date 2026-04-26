package org.satellite.dev.progiple.sateevents.event.realization.settings;

import com.sk89q.worldguard.protection.flags.*;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;

import java.util.HashMap;
import java.util.Map;

public record RegionSettings(int size, Map<Flag<?>, Object> flags) implements Settings {
    public static Map<Flag<?>, Object> getFlags(ConfigurationSection flagSection) {
        if (flagSection == null || GuardManager.flags() == null) return null;
        Map<Flag<?>, Object> flags = new HashMap<>();

        var registry = GuardManager.flags().getRegistry();
        flagSection.getKeys(false).forEach(key -> {
            var flag = registry.get(key);
            if (flag != null) {
                Object value = flagSection.get(key);
                flags.put(flag, unmarshalFlag(flag, value));
            }
        });

        return flags;
    }

    private static Object unmarshalFlag(Flag<?> flag, Object value) {
        if (value == null) return null;

        if (flag instanceof StateFlag) {
            if (value instanceof Boolean bool) {
                return bool ? StateFlag.State.ALLOW : StateFlag.State.DENY;
            }
            else {
                return Utils.getEnumValue(StateFlag.State.class, value.toString().toUpperCase(), StateFlag.State.DENY);
            }
        }

        if (flag instanceof DoubleFlag && value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (flag instanceof IntegerFlag && value instanceof Number) {
            return ((Number) value).intValue();
        }

        return value;
    }
}
