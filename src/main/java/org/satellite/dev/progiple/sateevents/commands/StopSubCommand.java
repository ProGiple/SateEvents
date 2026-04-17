package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Args;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.SateEventsManager;
import org.satellite.dev.progiple.sateevents.event.realization.IEventManager;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventStopReason;

import java.util.List;
import java.util.stream.Collectors;

@SubCommand(commandIdentifiers = {"stop"}, appliedCommand = "sateevents")
@Permissions("@.stop")
@Args(min = 2, max = 2)
public class StopSubCommand implements LunaExecutor {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        if (args[1].equalsIgnoreCase("all")) {
            SateEventsManager.stopAll(EventStopReason.FORCE);
            Config.sendMessage(sender, "stopAll");
            return;
        }

        var manager = SateEventsManager.getManager(args[1]);
        if (manager == null) {
            Config.sendMessage(sender, "eventNotExists", "id-%-" + args[1]);
            return;
        }

        if (!manager.isActive()) {
            Config.sendMessage(sender, "eventIsInactive", manager.getReplacementInformation(0));
            return;
        }

        manager.stop(EventStopReason.FORCE);
        Config.sendMessage(sender, "stop", manager.getReplacementInformation(0));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> list) {
        if (list.size() == 1) {
            List<String> result = SateEventsManager.getActiveManagers()
                    .map(IEventManager::getId)
                    .collect(Collectors.toList());
            result.add("all");
            return result;
        }

        return null;
    }
}

