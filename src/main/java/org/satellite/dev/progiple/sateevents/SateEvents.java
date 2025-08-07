package org.satellite.dev.progiple.sateevents;

import lombok.Getter;
import org.bukkit.Location;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.events.EventBar;
import org.satellite.dev.progiple.sateevents.events.EventManager;
import org.satellite.dev.progiple.sateevents.events.SateEvent;
import org.satellite.dev.progiple.sateevents.events.SateEventManager;
import org.satellite.dev.progiple.sateevents.listeners.OnBreakBlockHandler;
import org.satellite.dev.progiple.sateevents.listeners.OnClickOnBlockHandler;
import org.satellite.dev.progiple.sateevents.listeners.OnJoinLeaveHandler;

import java.time.LocalTime;

public final class SateEvents extends LunaPlugin {
    @Getter private static SateEvents INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        super.onEnable();

        LunaExecutor.initialize(this, "org.satellite.dev.progiple.sateevents.commands");
        this.registerListeners(new OnClickOnBlockHandler(), new OnBreakBlockHandler(), new OnJoinLeaveHandler());
        this.createPlaceholder("event", ((offlinePlayer, params) -> {
            if (params.equalsIgnoreCase("next_time")) {
                LocalTime localTime = SateEventManager.getNextTime(SateEventManager.getNext());
                return localTime == null ? "no" : Utils.Time.timeToString(localTime); // Время след. ивента
            }

            if (params.equalsIgnoreCase("next_name")) {
                EventManager eventManager = SateEventManager.getNext();
                return eventManager == null ? "no" : ColorManager.color(eventManager.getName()); // Имя след. ивента
            }

            if (params.equalsIgnoreCase("next_id")) {
                EventManager eventManager = SateEventManager.getNext();
                return eventManager == null ? "no" : eventManager.getLunaPlugin().getName(); // Айди плагина след. ивента
            }

            if (params.equalsIgnoreCase("next_left_time")) {
                LocalTime localTime = SateEventManager.getLeftTime(SateEventManager.getNext());
                return localTime == null ? "no" : Utils.Time.timeToString(localTime); // Время до след. ивента
            }

            if (params.equalsIgnoreCase("active")) {
                return SateEventManager.getLaunchedEvent() == null ? "no" : "yes"; // Активен ли сейчас любой ивент
            }

            SateEvent sateEvent = SateEventManager.getLaunchedEvent();
            if (params.equalsIgnoreCase("now_name")) {
                return sateEvent == null ? Config.getMessage("dropNotActive") : sateEvent.getName(); // Имя активного ивента
            }

            if (params.equalsIgnoreCase("now_id")) {
                return sateEvent == null ? Config.getMessage("dropNotActive") : sateEvent.getLunaPlugin().getName();
            }

            if (params.equalsIgnoreCase("left_time")) {
                return sateEvent == null ? Config.getMessage("dropNotActive") : Utils.Time.timeToString(
                        Utils.Time.parseTime(sateEvent.getDelay().getLeftSeconds()));
                // Время, которое осталось до окончания ивента
            }

            if (params.endsWith("[a]")) {
                return sateEvent == null ? Utils.setPlaceholders(offlinePlayer,
                        "%event_" + params.replace("[a]", "") + "%") :
                        Config.getMessage("dropIsActive"); // Проверка на активность ивента
            }

            Location location = sateEvent == null ? null : sateEvent.getLocation();
            if (params.equalsIgnoreCase("x")) {
                return location == null ? "---" : String.valueOf(location.getBlockX());
            }

            if (params.equalsIgnoreCase("y")) {
                return location == null ? "---" : String.valueOf(location.getBlockY());
            }

            if (params.equalsIgnoreCase("z")) {
                return location == null ? "---" : String.valueOf(location.getBlockZ());
            }

            if (params.equalsIgnoreCase("world")) {
                return location == null ? "---" : location.getWorld().getName();
            }
            return null;
        }));
    }

    @Override
    public void onDisable() {
        SateEvent sateEvent = SateEventManager.getLaunchedEvent();
        if (sateEvent != null) {
            EventBar eventBar = sateEvent.getEventBar();
            if (eventBar != null) eventBar.remove();
        }

        SateEventManager.remove();
        super.onDisable();
    }
}
