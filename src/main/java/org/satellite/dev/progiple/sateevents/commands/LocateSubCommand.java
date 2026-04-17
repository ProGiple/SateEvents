package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.SateEventsManager;
import org.satellite.dev.progiple.sateevents.event.realization.IEventManager;
import org.satellite.dev.progiple.sateevents.event.realization.ILocationStage;

import java.util.Comparator;
import java.util.List;

@SubCommand(commandIdentifiers = {"locate"}, appliedCommand = "sateevents")
@Permissions("@.locate")
public class LocateSubCommand implements LunaExecutor {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        IEventManager eventManager;
        if (args.length == 2) {
            eventManager = SateEventsManager.getManager(args[1]);
            if (eventManager == null) {
                Config.sendMessage(sender, "eventNotExists", "id-%-" + args[1]);
                return;
            }

            if (!eventManager.isActive() || !(eventManager.getLaunched().getStage() instanceof ILocationStage ls)) {
                Config.sendMessage(sender, "eventIsInactive", eventManager.getReplacementInformation(0));
                return;
            }
        }
        else {
            if (sender instanceof Player player) {
                var event = SateEventsManager.getNearestEvent(player.getLocation());
                if (event == null) {
                    Config.sendMessage(sender, "eventsAreInactive");
                    return;
                }

                eventManager = event.getManager();
            } else {
                eventManager = SateEventsManager.getActiveManagers()
                        .filter(m -> m.getLaunched().getStage() instanceof ILocationStage l &&
                                l.getLocation() != null)
                        .findFirst()
                        .orElse(null);
                if (eventManager == null) {
                    Config.sendMessage(sender, "eventsAreInactive");
                    return;
                }
            }
        }

        Config.sendMessage(sender, "locate", eventManager.getReplacementInformation(0));
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> list) {
        return list.size() == 1 ? Utils.tabCompleterFiltering(SateEventsManager.getActiveManagers()
                .map(IEventManager::getId), list.get(0)) : List.of();
    }
}