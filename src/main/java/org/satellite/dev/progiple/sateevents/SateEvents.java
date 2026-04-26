package org.satellite.dev.progiple.sateevents;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.CommandInitializer;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.service.managers.TaskManager;
import org.novasparkle.lunaspring.API.util.service.managers.worldguard.GuardManager;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
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
import org.satellite.dev.progiple.sateevents.timeParsers.Parser;
import org.satellite.dev.progiple.sateevents.timeParsers.ParserStorage;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public final class SateEvents extends LunaPlugin {
    @Getter private static SateEvents instance;

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();

        saveDefaultConfig();
        this.createPlaceholder();

        CommandInitializer.initialize(this, "#.commands");
        this.registerListeners(new OnClickOnBlockHandler(), new OnBreakBlockHandler(), new OnJoinLeaveHandler());

        boolean sateSchemsIsEnabled = Utils.isPluginEnabled("SateSchematics");
        if (sateSchemsIsEnabled) {
            Factories.register(new SateSchematicFactory());
        }

        EventUtils.Remover.removeBossBars();
        Bukkit.getScheduler().runTask(this, () -> this.clearDataInStart(sateSchemsIsEnabled));

        if (Config.getBoolean("enableEventScheduler")) {
            new EventStartScheduler().runTaskAsynchronously(this);
        }
    }

    @Override
    public void onDisable() {
        SateEventsManager.stopAll(EventStopReason.FORCE);
        TaskManager.stopAll(SateEventTimer.class, null);
        super.onDisable();
    }

    private void clearDataInStart(boolean sateSchemsIsEnabled) {
        File dir = new File(this.getDataFolder(), "saves/");
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    EventUtils.Remover.remove(file, sateSchemsIsEnabled);
                }
            }

            dir.delete();
        }

        EventUtils.Remover.removeRegions();
    }

    private void createPlaceholder() {
        this.createPlaceholder("sateevents", ((offlinePlayer, params) -> {
            if (params.startsWith("parse")) {
                Parser parser; // sateevents_parse-<parser>-<seconds>

                String[] split = params.split("-");
                if (split.length == 1) return null;

                long seconds;
                if (split.length == 2) {
                    parser = ParserStorage.getDefaultParser();
                    seconds = LunaMath.toLong(Utils.setBracketPlaceholders(offlinePlayer, split[1]));
                } else {
                    parser = ParserStorage.getParser(split[1]);
                    seconds = LunaMath.toLong(Utils.setBracketPlaceholders(offlinePlayer, split[2]));
                }

                return parser.parse(seconds);
            }

            var eventManager = SateEventsManager.getNextManager();
            if (eventManager == null) return Config.getString("placeholders.eventPoolIsEmpty");

            return switch (params) {
                case "next_time_seconds" -> String.valueOf(eventManager.secondsToNext(LocalDateTime.now()));
                case "next_time" ->
                        eventManager.getTimeParser().parseTime(eventManager.secondsToNext(LocalDateTime.now()));
                case "next_name" -> ColorManager.color(eventManager.getDisplayName()); // Имя след. ивента
                case "next_id" -> eventManager.getId(); // Айди плагина след. ивента
                default -> null;
            };
        }));
    }

    public static void executeThrowable(CommandSender sender, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            instance.async(t::printStackTrace);
            Config.sendMessage(sender, "actionThrows", "exception-%-" + t.getLocalizedMessage());
        }
    }
}
