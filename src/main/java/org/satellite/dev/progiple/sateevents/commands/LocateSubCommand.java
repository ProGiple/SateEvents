package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.events.SateEvent;
import org.satellite.dev.progiple.sateevents.events.SateEventManager;

@SubCommand(commandIdentifiers = {"locate"}, appliedCommand = "sateevents")
@Permissions("@.locate")
public class LocateSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        SateEvent lunaEvent = SateEventManager.getLaunchedEvent();
        if (lunaEvent == null) {
            Config.sendMessage(sender, "inactive");
            return;
        }

        Config.sendMessage(sender, "locate");
    }
}