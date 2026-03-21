package org.satellite.dev.progiple.sateevents;

import com.sk89q.worldguard.protection.managers.RegionManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.novasparkle.lunaspring.API.commands.CommandInitializer;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.novasparkle.lunaspring.API.util.utilities.Localization;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class SateEvents extends LunaPlugin {
    @Getter private static SateEvents instance;
    @Getter private static boolean sateSchematicsEnabled = false;

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();

        saveDefaultConfig();

        CommandInitializer.initialize(this, "#.commands");
        this.registerListeners(new OnClickOnBlockHandler(), new OnBreakBlockHandler(), new OnJoinLeaveHandler());

        Bukkit.getScheduler().runTaskAsynchronously(this, () ->
                sateSchematicsEnabled = Utils.isPluginEnabled("SateSchematics"));

        this.createPlaceholder("event", ((offlinePlayer, params) -> {
            if (params.contains("[tr]")) {
                String value = Utils.setNakedPlaceholders(offlinePlayer, "event_" + params.replace("[tr]", ""));

                String localize = Localization.localize(value);
                return localize == null ? value : ColorManager.color(localize);
            }

            if (params.startsWith("next_time")) {
                LocalTime localTime;

                String[] split = params.split("-");
                if (split.length <= 1) {
                    localTime = SateEventManager.getNextTime(SateEventManager.getNext());
                }
                else {
                    localTime = SateEventManager.getNextTime(SateEventManager.getManager(split[1]));
                }

                return localTime == null ? "no" : Utils.Time.timeToString(localTime); // Время след. ивента
            }

            if (params.startsWith("name-")) {
                EventManager eventManager = SateEventManager.getManager(params.replace("name-", ""));
                return eventManager == null ? "no" : ColorManager.color(eventManager.getName());
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
                String localTime = SateEventManager.getLeftTime(SateEventManager.getNext());
                return localTime == null ? "no" : localTime; // Время до след. ивента
            }

            SateEvent sateEvent = SateEventManager.getLaunchedEvent();
            if (params.equalsIgnoreCase("active")) {
                return sateEvent == null ? "no" : "yes"; // Активен ли сейчас любой ивент
            }

            if (params.endsWith("[a]")) {
                return sateEvent == null ?
                        Utils.setNakedPlaceholders(offlinePlayer, "event_" + params.replace("[a]", "")) :
                        Config.getMessage("dropIsActive");
            }

            if (params.endsWith("[!a]")) {
                return sateEvent != null ?
                        Utils.setNakedPlaceholders(offlinePlayer, "event_" + params.replace("[!a]", "")) :
                        Config.getMessage("dropNotActive");
            }

            if (sateEvent == null) {
                return Config.getMessage("dropNotActive");
            }

            if (params.equalsIgnoreCase("now_name")) {
                return sateEvent.getName(); // Имя активного ивента
            }

            if (params.equalsIgnoreCase("now_id")) {
                return sateEvent.getLunaPlugin().getName();
            }

            if (params.equalsIgnoreCase("left_time")) {
                return Utils.Time.timeToString(Utils.Time.parseTime(sateEvent.getDelay().getLeftSeconds()));
            }

            Location location = sateEvent.getLocation();
            if (location == null) {
                return "---";
            }

            if (params.equalsIgnoreCase("x")) {
                return String.valueOf(location.getBlockX());
            }

            if (params.equalsIgnoreCase("y")) {
                return String.valueOf(location.getBlockY());
            }

            if (params.equalsIgnoreCase("z")) {
                return String.valueOf(location.getBlockZ());
            }

            if (params.equalsIgnoreCase("world")) {
                return location.getWorld().getName();
            }
            return null;
        }));

        if (Utils.isPluginEnabled("SateSchematics")) {
            SSchem schem = new SSchem();
            schem.safeRemove();
        }

        Bukkit.getScheduler().runTask(this, () -> {
            for (RegionManager manager : GuardManager.getRegionContainer().getLoaded()) {
                Set<String> toRemove = manager.getRegions().keySet().stream()
                        .filter(id -> id.startsWith("sateevent-"))
                        .collect(Collectors.toSet());

                for (String id : toRemove) manager.removeRegion(id);
            }
        });

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

    @Override
    public void onDisable() {
        SateEvent sateEvent = SateEventManager.getLaunchedEvent();
        if (sateEvent != null) {
            SateEvent.Delay delay = sateEvent.getDelay();
            if (delay != null) delay.cancel();

            EventBar eventBar = sateEvent.getEventBar();
            if (eventBar != null) eventBar.delete();
        }

        SateEventManager.remove();
        super.onDisable();
    }
}
