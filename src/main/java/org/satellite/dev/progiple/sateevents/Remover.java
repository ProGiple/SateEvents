package org.satellite.dev.progiple.sateevents;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Remover {
    public void removeSchematic(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection section = config.getConfigurationSection("schem");
        if (section == null) return;

        new PastedSchematic(section).undoAsync();
    }

    public void removeBossBars() {
        List<NamespacedKey> keysToRemove = new ArrayList<>();
        Bukkit.getBossBars().forEachRemaining(b -> {
            if (b.getKey().getKey().startsWith("eventbar-")) {
                b.removeAll();
                keysToRemove.add(b.getKey());
            }
        });

        for (var key : keysToRemove) {
            Bukkit.removeBossBar(key);
        }
    }
}
