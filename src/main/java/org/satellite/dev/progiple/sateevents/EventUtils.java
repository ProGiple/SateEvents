package org.satellite.dev.progiple.sateevents;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.satellite.dev.progiple.sateevents.listeners.events.impl.ClearConfigAttemptEvent;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class EventUtils {
    public int getHighestNonAirY(World world, int x, int z) {
        for (int y = world.getMaxHeight() - 1; y >= world.getMinHeight(); y--) {
            Block block = world.getBlockAt(x, y, z);
            if (!block.getType().isAir()) {
                return y;
            }
        }
        return -1;
    }

    @UtilityClass
    public class Remover {
        public void remove(File file, boolean sateSchemsIsEnabled) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            var event = new ClearConfigAttemptEvent(file, config);
            event.callEvent();

            if (sateSchemsIsEnabled) removeSchematic(config.getConfigurationSection("schem"));
            removeBlocks(config.getConfigurationSection("blocks"));
        }

        public void removeSchematic(ConfigurationSection section) {
            if (section == null) return;
            new PastedSchematic(section).undoAsync();
        }

        public void removeBlocks(ConfigurationSection section) {
            if (section == null) return;
            for (String key : section.getKeys(false)) {
                String[] split = key.split(";");
                if (split.length < 4) continue;

                World world = Bukkit.getWorld(split[0]);
                if (world == null) continue;

                int x = LunaMath.toInt(split[1]);
                int y = LunaMath.toInt(split[2]);
                int z = LunaMath.toInt(split[3]);

                BlockData blockData = Bukkit.createBlockData(section.getString(key, ""));
                world.getBlockAt(x, y, z).setBlockData(blockData, false);
            }
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

        public void removeRegions() {
            for (var manager : GuardManager.getRegionContainer().getLoaded()) {
                Set<String> toRemove = manager.getRegions().keySet().stream()
                        .filter(id -> id.startsWith("sateevent-"))
                        .collect(Collectors.toSet());

                for (String id : toRemove) manager.removeRegion(id);
            }
        }
    }
}
