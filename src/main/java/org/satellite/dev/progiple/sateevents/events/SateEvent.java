package org.satellite.dev.progiple.sateevents.events;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.LFlag;
import org.novasparkle.lunaspring.API.util.utilities.LunaTask;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.sateevents.SSchem;
import org.satellite.dev.progiple.sateevents.SateEvents;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public abstract class SateEvent {
    @Setter private EventBar eventBar;
    @Setter private Location location;

    private final SSchem schem;
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
        this.schem = Utils.isPluginEnabled("SateSchematics") ? new SSchem() : null;
    }

    public abstract void create();
    public abstract void remove();

    public Location initLocation(World world,
                                 int maxX,
                                 int maxY,
                                 int minY,
                                 int maxZ,
                                 List<String> materials,
                                 List<String> blacklistBiomes,
                                 BlockFilter filter) {
        for (int i = 0; i < 15; i++) {
            Location location = Utils.findRandomLocation(world, maxX, maxZ);
            if (location == null) continue;

            Location upper = location.clone().add(0, 1, 0);
            if (upper.getY() < minY || upper.getY() > maxY || !upper.getBlock().getType().isAir()
                    || this.checkMaterial(location, materials, filter)
                    || this.checkRegion(upper, this.regionSize)
                    || this.checkBiome(upper, blacklistBiomes)) continue;

            return upper;
        }
        return null;
    }

    public Location initLocation(World world,
                                 int maxX,
                                 int maxY,
                                 int minY,
                                 int maxZ,
                                 List<String> materials,
                                 List<String> blacklistBiomes) {
        return this.initLocation(world, maxX, maxY, minY, maxZ, materials, blacklistBiomes, BlockFilter.BLACK);
    }

    public boolean checkRegion(Location location, int cuboidSize) {
        return cuboidSize > -1 && GuardManager.hasRegionsInside(location, cuboidSize + 1);
    }

    public boolean checkBiome(Location location, List<String> biomes) {
        return biomes != null && biomes.contains(location.getBlock().getBiome().name());
    }

    public boolean checkMaterial(Location location, List<String> list, BlockFilter filter) {
        String material = location.getBlock().getType().name();
        if (filter == BlockFilter.WHITE) return list == null || !list.contains(material);
        return list != null && list.contains(material);
    }

    public void insertSchematic(ConfigurationSection schemSection) {
        if (!schemSection.getBoolean("enabled")) return;

        String id = schemSection.getString("id");
        this.insertSchematic(id);
    }

    public void insertSchematic(String id) {
        if (this.schem != null && this.location != null) {
            Bukkit.getScheduler().runTask(SateEvents.getINSTANCE(), () -> this.schem.place(id, this.location));
        }
    }

    public void createRegion(int regionSize, List<String> flagList) {
        if (this.location == null) return;
        Location minLoc = this.location.clone().add(-regionSize, -regionSize, -regionSize);
        Location maxLoc = this.location.clone().add(regionSize, regionSize, regionSize);

        Bukkit.getScheduler().runTask(SateEvents.getINSTANCE(), () -> {
            ProtectedRegion region = GuardManager.createRegion(this.regionId, minLoc, maxLoc);
            if (flagList != null) flagList.forEach(f -> {
                String[] split = f.split(" <> ");
                if (split.length >= 2) region.setFlag(GuardManager.getWGFlag(LFlag.valueOf(split[0])), StateFlag.State.valueOf(split[1]));
            });
        });
    }

    public void removeRegion() {
        if (this.regionId != null) GuardManager.removeRegion(this.regionId);
    }

    public void removeSchematic() {
        if (this.schem != null) this.schem.undo();
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

    public enum BlockFilter {
        WHITE,
        BLACK
    }
}
