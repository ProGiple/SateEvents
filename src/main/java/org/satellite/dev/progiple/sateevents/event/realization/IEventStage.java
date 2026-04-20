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
    SateEvent getEvent();
    short getIndex();
    String getId();
    String getName();
    EventRequest stop(EventStopReason reason);
    EventRequest start(@Nullable String[] args);
    default String[] bossBarTitleReplacer(String title) {
        return getManager().getReplacementInformation(0);
    }
    EventBossBar getBossBar();
    void setBossBar(EventBossBar bossBar);

    default int getLifeTime() {
        return this.getTimer().getLifeTime();
    }

    default IEventManager getManager() {
        return this.getEvent().getManager();
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

    default void deleteBossBar() {
        if (this.getBossBar() != null) {
            this.getBossBar().delete();
            this.setBossBar(null);
        }
    }

    default void cancelTimer() {
        this.getTimer().cancel();
    }

    default void startTimer() {
        this.getTimer().runTaskAsynchronously(getEvent().getManager().getPlugin());
    }

    default void showBossBar() {
        if (this.getBossBar() != null) {
            SateEvent.playerIterator(getBossBar()::addPlayerWithConditions);
        }
    }

    default void timerTick(boolean isFinally) {
        if (getBossBar() != null) {
            getBossBar().update();
        }
    }

    default void pasteBlocks() {
        this.getBlocks().forEach(IEventBlock::place);
    }
}
