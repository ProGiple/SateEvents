package org.satellite.dev.progiple.sateevents.event.realization;

import jdk.jfr.Description;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.commands.LunaCompleter;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.API.util.utilities.localization.Localization;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.sateevents.timeParsers.TimeParser;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.EventPlaceholderRequest;
import org.satellite.dev.progiple.sateevents.event.EventTime;
import org.satellite.dev.progiple.sateevents.event.SateEventsManager;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventRequest;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventStopReason;
import org.satellite.dev.progiple.sateevents.event.realization.settings.EventSettings;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public interface IEventManager extends LunaCompleter, EventPlaceholderRequest {
    String getId();
    String getDisplayName();
    SateEvent getLaunched();
    void setLaunched(SateEvent sateEvent);
    LunaPlugin getPlugin();
    Collection<EventTime> getSpawnTimes();
    TimeParser getTimeParser();
    SateEvent startEvent(@Nullable CommandSender sender, @Nullable String[] args);

    default boolean isActive() {
        return this.getLaunched() != null;
    }

    default boolean isActive(SateEvent sateEvent) {
        return sateEvent != null && sateEvent.equals(this.getLaunched());
    }

    default boolean isActive(IEventStage stage) {
        return this.getLaunched() != null && this.getLaunched().getStage().equals(stage);
    }

    default boolean isActive(SateEvent event, IEventStage stage) {
        return this.isActive(event) && this.isActive(stage);
    }

    default boolean isActive(SateEvent event, int stageIndex) {
        return this.isActive(event) && event.getStage() != null && event.getStage().getIndex() == stageIndex;
    }

    default boolean isActive(SateEvent event, String stageId) {
        return isActive(event) && event.getStage() != null && event.getStage().getId().equals(stageId);
    }

    default boolean isActive(String eventId) {
        return this.getLaunched() != null && this.getLaunched().getSettings().getId().equals(eventId);
    }

    default boolean isActive(String eventId, String stageId) {
        return this.isActive(eventId) && this.isActive(this.getLaunched(), stageId);
    }

    default void registerEvent(SateEvent event) {
        this.setLaunched(event);
    }

    default SateEvent startEvent(@Nullable CommandSender sender) {
        return this.startEvent(sender, null);
    }

    default SateEvent startEvent() {
        return this.startEvent(null, null);
    }

    default void removeEvent() {
        this.setLaunched(null);
    }

    default EventRequest stop(EventStopReason reason) {
        return this.getLaunched() == null ? EventRequest.STAGE_IS_NOT_ACTIVE_NOW : this.getLaunched().stop(reason);
    }

    @Description("format: WEEK_DAY(or EVERYDAY) <hours>h <minutes>m <seconds>s")
    default void initializeTimeList(List<String> timeList) {
        for (String strTime : timeList) {
            this.getSpawnTimes().add(this.getTimeParser().parseTime(strTime));
        }
    }

    default boolean canStartNow(LocalDateTime source) {
        var time = timeToNext(source);
        return time != null && time.isNow(source);
    }

    default long secondsToNext(LocalDateTime source) {
        return this.getSpawnTimes()
                .stream()
                .map(e -> e.getSecondsFromNow(source))
                .min(Comparator.comparingLong(e -> e))
                .orElse(-1L);
    }

    default EventTime timeToNext(LocalDateTime source) {
        return this.getSpawnTimes()
                .stream()
                .min(Comparator.comparingLong(e -> e.getSecondsFromNow(source)))
                .orElse(null);
    }

    default String[] getReplacementInformation(int additionSize) {
        var event = this.getLaunched();
        var settings = event != null ? this.getLaunched().getSettings() : null;
        var stage = event != null ? this.getLaunched().getStage() : null;
        var location = stage instanceof ILocationStage ls ? ls.getLocation() : null;
        String[] result = {
                "id-%-" + this.getId(),
                "name-%-" + this.getDisplayName(),
                "plugin-%-" + this.getPlugin().getName(),
                "nextTime-%-" + getTimeParser().parseTime(secondsToNext(LocalDateTime.now())),
                "lifeTime-%-" + nullableString(event, SateEvent::getLifeTime),
                "active-%-" + (this.isActive() ? "yes" : "no"),
                "activeId-%-" + nullableString(settings, EventSettings::getId),
                "activeName-%-" + nullableString(settings, EventSettings::getName),
                "regionSize-%-" + nullableString(settings, s -> s.getRegionSettings().size()),
                "remainTime-%-" + nullableString(event, SateEvent::getRemainsSeconds),
                "regionId-%-" + nullableString(event, SateEvent::getRegionId),
                "stageIndex-%-" + nullableString(stage, IEventStage::getIndex),
                "stageId-%-" + nullableString(stage, IEventStage::getId),
                "stageName-%-" + nullableString(stage, IEventStage::getName),
                "leftTime-%-" + nullableString(stage, s -> s.getTimer().getTickTimes()),
                "x-%-" + nullableString(location, Location::getBlockX),
                "y-%-" + nullableString(location, Location::getBlockY),
                "z-%-" + nullableString(location, Location::getBlockZ),
                "world-%-" + nullableString(location, l -> l.getWorld().getName())
        };

        return additionSize <= 0 ? result : Arrays.copyOf(result, result.length + additionSize);
    }

    default <E> String nullableString(E checker, Function<E, Object> val) {
        if (checker == null) return "---";
        var result = val.apply(checker);
        if (result == null) return "---";
        return result.toString();
    }

    default void register() {
        SateEventsManager.getManagers().add(this);
    }

    default void unregister() {
        SateEventsManager.getManagers().remove(this);
    }

    @Override
    default String sendRequest(OfflinePlayer player, String argument, String params) {
        if (params.endsWith("[a]")) {
            String line = params.substring(0, params.length() - 3);
            if (this.isActive()) return Utils.setNakedPlaceholders(player, argument + "_" + line);
            return Config.getString("placeholders.dropNotActive");
        }

        if (params.endsWith("[!a]")) {
            String line = params.substring(0, params.length() - 4);
            if (!this.isActive()) return Utils.setNakedPlaceholders(player, argument + "_" + line);
            return Config.getString("placeholders.dropIsActive");
        }

        if (params.endsWith("[tr]")) {
            String line = params.substring(0, params.length() - 4);
            String value = Utils.setNakedPlaceholders(player, argument + "_" + line);

            String localize = Localization.translate("satevents." + value, () -> value);
            return localize == null ? value : ColorManager.color(localize);
        }

        switch (params) {
            case "is_next" -> {
                boolean isNext = this.equals(SateEventsManager.getNextManager());
                return Config.getString("placeholders.is" + (isNext ? "Next" : "NotNext"));
            }
            case "next_time" -> {
                return getTimeParser().parseTime(secondsToNext(LocalDateTime.now()));
            }
            case "next_time_seconds" -> {
                return String.valueOf(secondsToNext(LocalDateTime.now()));
            }
            case "name" -> {
                return this.getDisplayName();
            }
            case "id" -> {
                return this.getId();
            }
            case "plugin" -> {
                return this.getPlugin().getName();
            }
        }

        if (params.startsWith("active")) {
            boolean isActive = this.isActive();

            String[] rpl = params.split("_");
            if (rpl.length <= 1 || !isActive) {
                return Config.getString("placeholders." + (isActive ? "dropIsActive" : "dropNotActive"));
            }

            var event = this.getLaunched();
            switch (rpl[1]) {
                case "id" -> {
                    return event.getSettings().getId();
                }
                case "name" -> {
                    return event.getSettings().getName();
                }
                case "lifeTime" -> {
                    return String.valueOf(event.getLifeTime());
                }
                case "regionSize" -> {
                    return String.valueOf(event.getSettings().getRegionSettings().size());
                }
                case "remainTime" -> {
                    return String.valueOf(event.getRemainsSeconds());
                }
                case "regionId" -> {
                    return event.getRegionId();
                }
            }

        }

        if (params.startsWith("stage")) {
            String[] rpl = params.split("_");
            if (rpl.length <= 1) return null;

            IEventStage stage = this.isActive() ? this.getLaunched().getStage() : null;
            if (stage == null) {
                return Config.getString("placeholders.dropNotActive");
            }

            switch (rpl[1]) {
                case "id" -> {
                    return stage.getId();
                }
                case "name" -> {
                    return stage.getName();
                }
                case "index" -> {
                    return String.valueOf(stage.getIndex());
                }
                case "leftTime" -> {
                    return String.valueOf(stage.getTimer().getTickTimes());
                }
            }

            Location location = stage instanceof ILocationStage ls ? ls.getLocation() : null;
            if (location == null) {
                return Config.getString("placeholders.locationIsNull");
            }

            switch (rpl[1]) {
                case "world" -> {
                    return location.getWorld().getName();
                }
                case "x" -> {
                    return String.valueOf(location.getBlockX());
                }
                case "y" -> {
                    return String.valueOf(location.getBlockY());
                }
                case "z" -> {
                    return String.valueOf(location.getBlockZ());
                }
            }

        }

        return null;
    }
}
