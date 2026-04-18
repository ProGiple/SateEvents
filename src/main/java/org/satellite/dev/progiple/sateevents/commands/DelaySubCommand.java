package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.SateEventsManager;
import org.satellite.dev.progiple.sateevents.event.realization.IEventManager;

import java.util.List;

@SubCommand(commandIdentifiers = {"delay"}, appliedCommand = "sateevents")
@Permissions("@.delay")
public class DelaySubCommand implements LunaExecutor {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        if (args.length == 1) {
            IEventManager manager = SateEventsManager.getNextManager();
            if (manager == null) {
                Config.sendMessage(sender, "eventPoolIsEmpty");
                return;
            }

            Config.sendMessage(sender, "delayNext.global", manager.getReplacementInformation(0));
            return;
        }

        if (args.length >= 2) {
            String id = args[1];

            IEventManager eventManager = SateEventsManager.getManager(id);
            if (eventManager == null) {
                Config.sendMessage(sender, "eventNotExists", "id-%-" + args[1]);
                return;
            }

            if (SateEventsManager.isActive()) {
                Config.sendMessage(sender, "eventIsActive", eventManager.getReplacementInformation(0));
                return;
            }

            Config.sendMessage(sender, "delayNext.taped", eventManager.getReplacementInformation(0));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> list) {
        return list.size() == 1 ? Utils.tabCompleterFiltering(SateEventsManager.getManagers()
                .stream()
                .map(IEventManager::getId), list.get(0)) : List.of();
    }
}

