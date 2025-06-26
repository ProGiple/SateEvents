package org.satellite.dev.progiple.sateevents.events;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.service.managers.WorldEditManager;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.LFlag;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.RegionManager;
import org.novasparkle.lunaspring.API.util.utilities.LunaTask;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.sateevents.SateEvents;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public abstract class SateEvent {
    @Setter private EventBar eventBar;
    @Setter private Location location;
    private EditSession editSession;

    private final Set<EventBlock> eventBlocks = new HashSet<>();
    private final LunaPlugin lunaPlugin;
    private final int regionSize;
    private final String regionId;
    private final Delay delay;
    private final String name;
    public SateEvent(LunaPlugin lunaPlugin, int lifeTime, String name, int regionSize) {
        this.lunaPlugin = lunaPlugin;
        this.delay = new Delay(lifeTime);
        this.name = ColorManager.color(name);
        this.regionId = "drop-" + Utils.getRKey((byte) 12);
        this.regionSize = regionSize;
    }

    public abstract void create();
    public abstract void remove();

    public Location initLocation(World world,
                                 int maxX,
                                 int maxY,
                                 int minY,
                                 int maxZ,
                                 List<String> blacklistMaterials,
                                 List<String> blacklistBiomes) {
        for (int i = 0; i < 15; i++) {
            Location location = Utils.findRandomLocation(world, maxX, maxZ);
            if (location == null) continue;

            Location upper = location.clone().add(0, 1, 0);
            if (upper.getY() < minY || upper.getY() > maxY || !upper.getBlock().getType().isAir()
                    || this.checkBlacklist(location, blacklistMaterials)
                    || this.checkRegion(upper, this.regionSize)
                    || this.checkBiome(upper, blacklistBiomes)) continue;

            return upper;
        }
        return null;
    }

    public boolean checkRegion(Location location, int cuboidSize) {
        return cuboidSize > -1 && RegionManager.hasRegionsInside(location, cuboidSize + 1);
    }

    public boolean checkBiome(Location location, List<String> biomes) {
        return biomes != null && biomes.contains(location.getBlock().getBiome().name());
    }

    public boolean checkBlacklist(Location location, List<String> blacklist) {
        return blacklist != null && blacklist.contains(location.getBlock().getType().name());
    }

    public void insertSchematic(ConfigurationSection schemSection, File schemDir) {
        if (!schemSection.getBoolean("enabled") || this.location == null || this.editSession != null) return;

        File file = new File(schemDir, String.format("%s.schem", schemSection.getString("id")));
        if (!file.exists() || file.isDirectory()) return;

        ConfigurationSection offsets = schemSection.getConfigurationSection("offsets");
        assert offsets != null;
        Bukkit.getScheduler().runTask(SateEvents.getINSTANCE(), () -> this.editSession = WorldEditManager.pasteSchematic(
                file, this.location,
                offsets.getInt("x"),
                offsets.getInt("y"),
                offsets.getInt("z"),
                offsets.getBoolean("ignore_air_blocks")));
    }

    public void createRegion(int regionSize, List<String> flagList) {
        if (this.location == null) return;
        Location minLoc = this.location.clone().add(-regionSize, -regionSize, -regionSize);
        Location maxLoc = this.location.clone().add(regionSize, regionSize, regionSize);

        Bukkit.getScheduler().runTask(SateEvents.getINSTANCE(), () -> {
            RegionManager.createRegion(this.regionId, minLoc, maxLoc);

            ProtectedRegion region = RegionManager.getRegion(this.regionId);
            if (flagList != null) flagList.forEach(f -> {
                String[] split = f.split(" <> ");
                if (split.length >= 2) region.setFlag(LFlag.valueOf(split[0]).getStateFlag(), StateFlag.State.valueOf(split[1]));
            });
        });
    }

    public void removeRegion() {
        if (this.regionId != null) RegionManager.removeRegion(this.regionId);
    }

    public void removeSchematic() {
        if (this.editSession != null && this.location != null) WorldEditManager.undo(this.editSession, this.location.getWorld());
    }

    @Getter
    public class Delay extends LunaTask {
        private final int max;
        private int leftSeconds;
        public Delay(int seconds) {
            super(0);
            this.max = seconds;
            this.leftSeconds = seconds;
        }

        @Override @SneakyThrows @SuppressWarnings("all")
        public void start() {
            while (this.leftSeconds > 0) {
                if (!this.isActive() || !SateEventManager.getLaunchedEvent().equals(SateEvent.this)) return;

                this.leftSeconds--;
                if (SateEvent.this.eventBar != null) SateEvent.this.eventBar.update();

                Thread.sleep(1000L);
            }

            SateEventManager.remove();
        }
    }
}
