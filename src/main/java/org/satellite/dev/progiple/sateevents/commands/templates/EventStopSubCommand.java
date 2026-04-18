package org.satellite.dev.progiple.sateevents.commands.templates;

import org.bukkit.command.CommandSender;
import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.realization.IEventManager;
import org.satellite.dev.progiple.sateevents.event.realization.impl.EventStopReason;

import java.util.List;

public class EventStopSubCommand extends AbstractTemplateSubCommand {
    public EventStopSubCommand(IEventManager manager) {
        super(manager);
    }

    @Override
    public void invoke(CommandSender sender, String[] strings) {
        if (!manager.isActive()) {
            Config.sendMessage(sender, "eventIsInactive", manager.getReplacementInformation(0));
            return;
        }

        SateEvents.executeThrowable(sender, () -> {
            manager.stop(EventStopReason.FORCE);
            Config.sendMessage(sender, "stop", manager.getReplacementInformation(0));
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> list) {
        return null;
    }
}
