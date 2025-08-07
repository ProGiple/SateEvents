package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.LunaCompleter;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;
import org.novasparkle.lunaspring.LunaSpring;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.events.EventManager;
import org.satellite.dev.progiple.sateevents.events.SateEventManager;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@SubCommand(commandIdentifiers = {"delay"}, appliedCommand = "sateevents")
@Check(permissions = "sateevents.delay", flags = {})
public class DelaySubCommand implements LunaCompleter {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Config.sendMessage(sender, "delayNext");
            return;
        }

        if (args.length >= 2) {
            LunaPlugin lunaPlugin = LunaSpring.getInstance().getLunaPlugin(args[1]);
            if (lunaPlugin == null) {
                Config.sendMessage(sender, "notExists", "event_name-%-" + args[1]);
                return;
            }

            EventManager eventManager = SateEventManager.getManager(lunaPlugin);
            if (eventManager == null) {
                Config.sendMessage(sender, "notExists", "event_name-%-" + args[1]);
                return;
            }

            if (SateEventManager.getLaunchedEvent() != null) {
                Config.sendMessage(sender, "isActive", "event_name-%-" + eventManager.getName());
                return;
            }

            LocalTime next = Utils.Time.getNextTime(eventManager.getTimes());
            Config.sendMessage(sender, "delay", "event_name-%-" + eventManager.getName(),
                    "event_time-%-" + next.toString(),
                    "event_time_left-%-" + Utils.Time.getTimeBetween(LocalTime.now(), next).toString());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> list) {
        return list.size() == 1 ? Utils.tabCompleterFiltering(SateEventManager.getRegisteredManagers()
                .stream()
                .map(m -> m.getLunaPlugin().getName())
                .collect(Collectors.toSet()), list.get(0)) : List.of();
    }
}

