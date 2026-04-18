package org.satellite.dev.progiple.sateevents.event.realization;

import org.jetbrains.annotations.Nullable;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventBossBar;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventRequest;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventStopReason;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventTimer;

import java.util.Collection;

public interface IEventStage {
    Collection<IEventBlock> getBlocks();
    EventTimer getTimer();
    short getIndex();
    String getId();
    String getName();
    EventRequest stop(EventStopReason reason);
    EventRequest start(@Nullable String[] args);
    String[] bossBarTitleReplacer(String title);
    EventBossBar getBossBar();

    default int getLifeTime() {
        return this.getTimer().getLifeTime();
    }

    default SateEvent getEvent() {
        return this.getTimer().getEvent();
    }

    default void registerBlock(IEventBlock block) {
        this.getBlocks().add(block);
    }

    default void unregisterBlock(IEventBlock block) {
        this.getBlocks().remove(block);
    }

    default void clearBlocks() {
        this.getBlocks().clear();
    }

    default void removeBlock(IEventBlock block) {
        unregisterBlock(block);
        block.destroy();
    }

    default void removeBlocks() {
        this.getBlocks().forEach(IEventBlock::destroy);
        this.getBlocks().clear();
    }

    default void pasteBlock(IEventBlock block) {
        registerBlock(block);
        block.place();
    }

    default void pasteBlocks() {
        this.getBlocks().forEach(IEventBlock::place);
    }
}
