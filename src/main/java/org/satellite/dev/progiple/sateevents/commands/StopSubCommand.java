package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.LunaSpring;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.events.EventManager;
import org.satellite.dev.progiple.sateevents.events.SateEvent;
import org.satellite.dev.progiple.sateevents.events.SateEventManager;

@SubCommand(commandIdentifiers = {"stop"}, appliedCommand = "sateevents")
@Check(permissions = "sateevents.stop", flags = {})
public class StopSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        SateEvent activeEvent = SateEventManager.getLaunchedEvent();
        EventManager eventManager = args.length == 1 ? (activeEvent == null ? null :
                SateEventManager.getManager(activeEvent.getLunaPlugin())) :
                SateEventManager.getManager(LunaSpring.getInstance().getLunaPlugin(args[1]));

        if (eventManager == null || activeEvent == null) {
            Config.sendMessage(sender, "inactive");
            return;
        }

        SateEventManager.remove();
        Config.sendMessage(sender, "events.stop", "event_name-%-" + eventManager.getName());
    }
}

