package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.commands.processor.NoArgCommand;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.SateEventsManager;
import org.satellite.dev.progiple.sateevents.event.realization.ILocationStage;
import org.satellite.dev.progiple.sateevents.event.realization.SateEvent;

@SubCommand(commandIdentifiers = {"compass"}, appliedCommand = "sateevents")
@Check(permissions = "@.compass", flags = NoArgCommand.AccessFlag.PLAYER_ONLY)
public class CompassSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() != Material.COMPASS) {
            Config.sendMessage(player, "noCompass");
            return;
        }

        SateEvent sateEvent = SateEventsManager.getNearestEvent(player.getLocation());
        if (sateEvent == null) {
            Config.sendMessage(sender, "eventsAreInactive");
            return;
        }

        CompassMeta compassMeta = (CompassMeta) itemStack.getItemMeta();
        compassMeta.setLodestone(((ILocationStage) sateEvent.getStage()).getLocation());
        compassMeta.setLodestoneTracked(false);
        itemStack.setItemMeta(compassMeta);

        Config.sendMessage(player, "compass", sateEvent.getManager().getReplacementInformation(0));
    }
}
