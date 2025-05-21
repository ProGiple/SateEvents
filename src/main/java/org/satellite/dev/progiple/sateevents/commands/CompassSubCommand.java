package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.LunaSpringSubCommand;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.events.SateEvent;
import org.satellite.dev.progiple.sateevents.events.SateEventManager;

@SubCommand(commandIdentifiers = {"compass"}, appliedCommand = "sateevents")
@Check(permissions = "sateevents.compass", flags = LunaSpringSubCommand.AccessFlag.PLAYER_ONLY)
public class CompassSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() != Material.COMPASS) {
            Config.sendMessage(player, "noCompass");
            return;
        }

        SateEvent sateEvent = SateEventManager.getLaunchedEvent();
        if (sateEvent == null) {
            Config.sendMessage(sender, "inactive");
            return;
        }

        player.setCompassTarget(sateEvent.getLocation());
        Config.sendMessage(player, "compass");
    }
}
