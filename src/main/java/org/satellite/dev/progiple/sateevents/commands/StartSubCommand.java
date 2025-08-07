package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.LunaCompleter;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;
import org.novasparkle.lunaspring.LunaSpring;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.events.EventManager;
import org.satellite.dev.progiple.sateevents.events.SateEventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SubCommand(commandIdentifiers = {"start"}, appliedCommand = "sateevents")
@Check(permissions = "sateevents.start", flags = {})
public class StartSubCommand implements LunaCompleter {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<EventManager> list = new ArrayList<>(SateEventManager.getRegisteredManagers());

            EventManager manager = list.isEmpty() ? null : list.get(LunaMath.getRandomInt(0, list.size()));
            if (manager == null) return;

            if (!SateEventManager.run(manager)) {
                Config.sendMessage(sender, "isActive", "event_name-%-" + manager.getName());
                return;
            }

            Config.sendMessage(sender, "startRandom");
            return;
        }

        LunaPlugin lunaPlugin = LunaSpring.getInstance().getLunaPlugin(args[1]);
        if (lunaPlugin == null) {
            Config.sendMessage(sender, "notExists", "event_name-%-" + args[1], args[2]);
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

        SateEventManager.run(eventManager);
        Config.sendMessage(sender, "start", "event_name-%-" + eventManager.getName());
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> args) {
        return args.size() == 1 ? Utils.tabCompleterFiltering(SateEventManager.getRegisteredManagers()
                .stream()
                .map(m -> m.getLunaPlugin().getName())
                .collect(Collectors.toSet()), args.get(0)) : List.of();
    }
}
