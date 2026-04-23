package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.commands.annotations.TabCompleteIgnore;
import org.novasparkle.lunaspring.API.commands.processor.NoArgCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.SateEventsManager;
import org.satellite.dev.progiple.sateevents.event.realization.IEventManager;
import org.satellite.dev.progiple.sateevents.event.realization.ILocationStage;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;

import java.util.List;

@SubCommand(commandIdentifiers = {"teleport", "tp"}, appliedCommand = "sateevents")
@TabCompleteIgnore("tp")
@Check(permissions = "@.teleport", flags = NoArgCommand.AccessFlag.PLAYER_ONLY)
public class TeleportSubCommand implements LunaExecutor {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        SateEvent event;
        if (args.length == 1) {
            event = SateEventsManager.getNearestEvent(player.getLocation());
            if (event == null) {
                Config.sendMessage(sender, "eventsAreInactive");
                return;
            }
        }
        else {
            var manager = SateEventsManager.getManager(args[1]);
            if (manager == null) {
                Config.sendMessage(sender, "eventNotExists", "id-%-" + args[1]);
                return;
            }

            if (!manager.isActive() ||
                    !(manager.getLaunched().getStage() instanceof ILocationStage ls) ||
                    ls.getLocation() == null) {
                Config.sendMessage(sender, "eventIsInactive", manager.getReplacementInformation(0));
                return;
            }

            event = manager.getLaunched();
        }

        player.teleport(((ILocationStage) event.getStage()).getLocation().clone().add(0, 1.25, 0));
        Config.sendMessage(player, "teleport", event.getManager().getReplacementInformation(0));
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> list) {
        return list.size() == 1 ? Utils.tabCompleterFiltering(SateEventsManager.getActiveManagers()
                .map(IEventManager::getId), list.get(0)) : List.of();
    }
}
