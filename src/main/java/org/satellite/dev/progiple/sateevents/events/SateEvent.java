package org.satellite.dev.progiple.sateevents.events;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.LFlag;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.API.util.utilities.tasks.LunaTask;
import org.novasparkle.lunaspring.API.util.utilities.tasks.Runnable;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.sateevents.SSchem;
import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.configs.Config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Getter @Setter
public abstract class SateEvent {
    protected EventBar eventBar;
    protected Location location;
    protected SSchem schem;
    protected int regionSize;
    protected String regionId;
    protected String name;

    protected final Set<EventBlock> eventBlocks = new HashSet<>();
    protected final LunaPlugin lunaPlugin;
    protected final Delay delay;
    public SateEvent(LunaPlugin lunaPlugin, int lifeTime, String name, int regionSize, Supplier<SSchem> sSchemSupplier) {
        this.lunaPlugin = lunaPlugin;
        this.delay = new Delay(lifeTime);
        this.name = ColorManager.color(name);
        this.regionId = "sateevent-" + Utils.getRKey((byte) 12);
        this.regionSize = regionSize;
        this.schem = Utils.isPluginEnabled("SateSchematics") ? sSchemSupplier.get() : null;
    }

    public SateEvent(LunaPlugin lunaPlugin, int lifeTime, String name, int regionSize) {
        this(lunaPlugin, lifeTime, name, regionSize, SSchem::new);
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
        return Utils.findRandomLocations(world, maxX, maxZ, Math.max(Config.getFindLocAtt(), 15), location -> {
            Location upper = location.add(0, 1, 0);
            return !(upper.getY() < minY) && !(upper.getY() > maxY) && upper.getBlock().getType().isAir()
                    && !this.checkMaterial(location, materials, filter)
                    && !this.checkRegion(upper, this.regionSize)
                    && !this.checkBiome(upper, blacklistBiomes);
        });
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

    public Location initLocation(ConfigurationSection section, World world) {
        int maxX = section.getInt("maxX");
        int maxY = section.getInt("maxY");
        int maxZ = section.getInt("maxZ");
        int minY = section.getInt("minY");
        List<String> biome_blacklist = section.getStringList("invalid_biomes");
        List<String> materials = section.getStringList("filter_blocks");
        BlockFilter filter = Utils.getEnumValue(BlockFilter.class, section.getString("block_filter"), BlockFilter.BLACK);

        ConfigurationSection schemSection = section.getConfigurationSection("schematic");
        int offsetX = 0;
        int offsetY = 0;
        int offsetZ = 0;
        if (schemSection != null) {
            offsetY = schemSection.getInt("y");
            offsetZ = schemSection.getInt("z");
            offsetX = schemSection.getInt("x");
        }

        return this.initLocation(world, maxX, maxY, minY, maxZ, materials, biome_blacklist, filter).add(offsetX, offsetY, offsetZ);
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
            this.schem.place(id, this.location);
        }
    }

    public void createRegion(int regionSize, List<String> flagList) {
        if (this.location == null) return;
        Location minLoc = this.location.clone().add(-regionSize, -regionSize, -regionSize);
        Location maxLoc = this.location.clone().add(regionSize, regionSize, regionSize);

        ProtectedRegion region = GuardManager.createRegion(this.regionId, minLoc, maxLoc);
        if (flagList != null) flagList.forEach(f -> {
            String[] split = f.split(" <> ");
            if (split.length >= 2) region.setFlag(GuardManager.getWGFlag(LFlag.valueOf(split[0])), StateFlag.State.valueOf(split[1]));
        });
    }

    public void removeRegion() {
        if (this.regionId != null) GuardManager.removeRegion(this.regionId);
    }

    public void removeSchematic() {
        if (this.schem != null) this.schem.undo();
    }

    public void timerAction(Delay delay) {
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
                if (!this.isActive() || !SateEvent.this.equals(SateEventManager.getLaunchedEvent())) return;

                this.leftSeconds--;
                if (SateEvent.this.eventBar != null) SateEvent.this.eventBar.update();

                timerAction(this);
                Thread.sleep(1000L);
            }

            Runnable.start(() -> SateEventManager.remove()).runTask(SateEvents.getINSTANCE());
        }
    }

    public enum BlockFilter {
        WHITE,
        BLACK
    }
}
