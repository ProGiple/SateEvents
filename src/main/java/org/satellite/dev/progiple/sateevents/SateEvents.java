package org.satellite.dev.progiple.sateevents;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.CommandInitializer;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.service.managers.TaskManager;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.EventStartScheduler;
import org.satellite.dev.progiple.sateevents.event.SateEventTimer;
import org.satellite.dev.progiple.sateevents.event.SateEventsManager;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventStopReason;
import org.satellite.dev.progiple.sateevents.factories.impl.SateSchematicFactory;
import org.satellite.dev.progiple.sateevents.factories.storage.Factories;
import org.satellite.dev.progiple.sateevents.listeners.OnBreakBlockHandler;
import org.satellite.dev.progiple.sateevents.listeners.OnClickOnBlockHandler;
import org.satellite.dev.progiple.sateevents.listeners.OnJoinLeaveHandler;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class SateEvents extends LunaPlugin {
    @Getter private static SateEvents instance;
    @Getter private static boolean sateSchematicsEnabled = false;

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();

        saveDefaultConfig();
        this.createPlaceholder();

        CommandInitializer.initialize(this, "#.commands");
        this.registerListeners(new OnClickOnBlockHandler(), new OnBreakBlockHandler(), new OnJoinLeaveHandler());

        Remover.removeBossBars();
        Bukkit.getScheduler().runTask(this, () -> {
            if (Utils.isPluginEnabled("SateSchematics")) {
                sateSchematicsEnabled = true;
                this.clearSchematicsInStart();
            }

            for (var manager : GuardManager.getRegionContainer().getLoaded()) {
                Set<String> toRemove = manager.getRegions().keySet().stream()
                        .filter(id -> id.startsWith("sateevent-"))
                        .collect(Collectors.toSet());

                for (String id : toRemove) manager.removeRegion(id);
            }
        });

        if (Config.getBoolean("enableEventScheduler")) {
            new EventStartScheduler().runTaskAsynchronously(this);
        }

        Factories.getFactoryClass(SateSchematicFactory.class);
    }

    @Override
    public void onDisable() {
        SateEventsManager.stopAll(EventStopReason.FORCE);
        TaskManager.stopAll(SateEventTimer.class, null);
        super.onDisable();
    }

    private void clearSchematicsInStart() {
        File dir = new File(this.getDataFolder(), "saves/");
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    Remover.removeSchematic(file);
                }
            }

            dir.delete();
        }
    }

    private void createPlaceholder() {
        this.createPlaceholder("sateevents", ((offlinePlayer, params) -> {
            if (params.startsWith("next_time")) {
                var eventManager = SateEventsManager.getNextManager();
                return eventManager == null ? Config.getString("placeholders.eventPoolIsEmpty") :
                        eventManager.getTimeParser().parseTime(eventManager.secondsToNext(LocalDateTime.now()));
            }

            if (params.equalsIgnoreCase("next_name")) {
                var eventManager = SateEventsManager.getNextManager();
                return eventManager == null ? Config.getString("placeholders.eventPoolIsEmpty") :
                        ColorManager.color(eventManager.getDisplayName()); // Имя след. ивента
            }

            if (params.equalsIgnoreCase("next_id")) {
                var eventManager = SateEventsManager.getNextManager();
                return eventManager == null ? Config.getString("placeholders.eventPoolIsEmpty") :
                        eventManager.getId(); // Айди плагина след. ивента
            }

            return null;
        }));
    }

    public static void executeThrowable(CommandSender sender, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            Config.sendMessage(sender, "actionThrows", "exception-%-" + throwable.getLocalizedMessage());
        }
    }
}
