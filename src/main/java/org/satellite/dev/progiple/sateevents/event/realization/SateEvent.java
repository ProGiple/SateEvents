package org.satellite.dev.progiple.sateevents.event.realization;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.satellite.dev.progiple.sateevents.TimeParser;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventRequest;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventStopReason;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventTimer;
import org.satellite.dev.progiple.sateevents.event.realization.settings.EventSettings;
import org.satellite.dev.progiple.sateevents.listeners.events.impl.NextStageEvent;
import org.satellite.dev.progiple.sateevents.listeners.events.impl.SateEventStartEvent;
import org.satellite.dev.progiple.sateevents.listeners.events.impl.SateEventStopEvent;

import java.util.function.Consumer;

@Getter
public abstract class SateEvent {
    private final IEventManager manager;
    private EventSettings settings;
    private IEventStage stage;
    protected short startIndex = 0;
    public SateEvent(IEventManager manager) {
        this.manager = manager;
    }

    public SateEvent(IEventManager manager, EventSettings settings) {
        this.manager = manager;
        this.settings = settings;
    }

    public TimeParser getTimeParser() {
        return manager.getTimeParser();
    }

    public int getRemainsSeconds() {
        if (stage == null || stage.getTimer() == null) return settings.getLifeSeconds();
        return settings.getLifeSeconds() - stage.getTimer().getTickTimes();
    }

    public EventRequest stop(EventStopReason reason) {
        if (this.stage == null)
            return EventRequest.STAGE_IS_NOT_ACTIVE_NOW;

        var request = this.stage.stop(reason);
        this.stage = null;

        this.manager.removeEvent();

        this.removeRegion();
        this.removeAllSchematics();

        var eventStopEvent = new SateEventStopEvent(this, reason, request);
        eventStopEvent.call();
        return eventStopEvent.getRequest();
    }

    public EventRequest start(@Nullable String[] args) {
        if (this.manager.isActive(this) || this.manager.isActive(this.getSettings().getId())) {
            return EventRequest.EVENT_IS_ACTIVE_NOW;
        }

        if (this.stage != null) return EventRequest.STAGE_IS_ACTIVE_NOW;

        IEventStage stage = stageFactory(startIndex, args);
        if (stage == null) return EventRequest.STAGE_POOL_IS_EMPTY;

        var startEvent = new SateEventStartEvent(this, stage);
        if (!startEvent.call()) return EventRequest.EVENT_IS_CANCELLED;
        stage = startEvent.getStage();

        this.stage = stage;
        this.manager.registerEvent(this);
        return this.stage.start(args);
    }

    public EventRequest nextStage() {
        if (this.stage == null) {
            return EventRequest.STAGE_IS_NOT_ACTIVE_NOW;
        }

        short index = this.stage.getIndex();
        index++;

        IEventStage stage = stageFactory(index, null);
        if (stage == null) return EventRequest.STAGE_POOL_IS_EMPTY;

        var nextStageEvent = new NextStageEvent(this, stage);
        if (!nextStageEvent.call()) return EventRequest.EVENT_IS_CANCELLED;
        stage = nextStageEvent.getStage();

        this.stage.stop(EventStopReason.NEXT_STAGE);

        this.stage = stage;
        return this.stage.start(null);
    }

    public String getRegionId() {
        return "sateevents-" + this.settings.getId();
    }

    public EventRequest createRegion(@NotNull Location location, @NotNull String regionId) {
        var region = this.settings.getRegionSettings();
        if (region.size() <= 0) return EventRequest.REGIONS_ARE_DISABLED;

        int regionSize = region.size();

        BlockVector3 minVector = BlockVector3.at(
                location.getBlockX() - regionSize,
                location.getBlockY() - regionSize,
                location.getBlockZ() - regionSize);
        BlockVector3 maxVector = BlockVector3.at(
                location.getBlockX() + regionSize,
                location.getBlockY() + regionSize,
                location.getBlockZ() + regionSize);

        var cuboid = new ProtectedCuboidRegion(regionId, minVector, maxVector);
        if (region.flags() != null) cuboid.setFlags(region.flags());
        GuardManager.getRegionManager(location.getWorld()).addRegion(cuboid);

        return EventRequest.SUCCESS;
    }

    public EventRequest createRegion(@NotNull String rgId) {
        Location location = this.stage instanceof ILocationStage lc ? lc.getLocation() : null;
        if (location == null) return EventRequest.LOCATION_NOT_GENERATED;

        return createRegion(location, rgId);
    }

    public EventRequest createRegion() {
        return createRegion(getRegionId());
    }

    public void removeRegion(String rgId) {
        GuardManager.removeRegion(rgId);
    }

    public void removeRegion() {
        removeRegion(getRegionId());
    }

    public EventRequest pasteSchematic(Location location) {
         var schemSettings = this.settings.getSchematicSettings();
         if (schemSettings == null) return EventRequest.SCHEMATICS_ARE_DISABLED;

         var result = schemSettings.pasteRandom(this, location);
         return result == null ? EventRequest.SCHEMATIC_IS_NULL : EventRequest.SUCCESS;
    }

    public EventRequest pasteSchematicAsync(Location location) {
        var schemSettings = this.settings.getSchematicSettings();
        if (schemSettings == null) return EventRequest.SCHEMATICS_ARE_DISABLED;

        schemSettings.pasteAsyncRandom(this, location);
        return EventRequest.SUCCESS;
    }

    public EventRequest removeAllSchematics() {
        var schemSettings = this.settings.getSchematicSettings();
        if (schemSettings == null) return EventRequest.SCHEMATICS_ARE_DISABLED;

        schemSettings.removeAll(this);
        return EventRequest.SUCCESS;
    }

    protected abstract IEventStage stageFactory(short index, @Nullable String[] args);

    public abstract void timerTick(boolean isFinally, EventTimer timer);

    public static void playerIterator(Consumer<Player> playerConsumer) {
        var list = Bukkit.getOnlinePlayers();
        if (list.size() > 9500)
            list.parallelStream().forEach(playerConsumer);
        else
            list.forEach(playerConsumer);
    }
}
