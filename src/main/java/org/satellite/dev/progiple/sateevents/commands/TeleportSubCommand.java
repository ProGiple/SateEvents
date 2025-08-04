package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.ZeroArgCommand;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.events.SateEvent;
import org.satellite.dev.progiple.sateevents.events.SateEventManager;

@SubCommand(commandIdentifiers = {"teleport", "tp"}, appliedCommand = "sateevents")
@Check(permissions = "sateevents.teleport", flags = ZeroArgCommand.AccessFlag.PLAYER_ONLY)
public class TeleportSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        SateEvent sateEvent = SateEventManager.getLaunchedEvent();
        if (sateEvent == null) {
            Config.sendMessage(sender, "inactive");
            return;
        }

        Player player = (Player) sender;
        player.teleport(sateEvent.getLocation().clone().add(0, 1.25, 0));
        Config.sendMessage(player, "teleport");
    }
}
