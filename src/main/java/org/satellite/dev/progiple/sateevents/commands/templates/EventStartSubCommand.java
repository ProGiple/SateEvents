package org.satellite.dev.progiple.sateevents.commands.templates;

import org.bukkit.command.CommandSender;
import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.realization.IEventManager;

import java.util.List;

public class EventStartSubCommand extends AbstractTemplateSubCommand {
    // ev start args

    public EventStartSubCommand(IEventManager manager) {
        super(manager);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> list) {
        return manager.tabComplete(sender, list);
    }

    @Override
    public void invoke(CommandSender sender, String[] strings) {
        if (manager.isActive()) {
            Config.sendMessage(sender, "eventIsActive", manager.getReplacementInformation(0));
            return;
        }

        String[] summonArgs;
        if (strings.length >= 2) {
            summonArgs = new String[strings.length - 1];
            System.arraycopy(strings, 1, summonArgs, 0, strings.length - 1);
        } else {
            summonArgs = null;
        }

        SateEvents.executeThrowable(sender, () -> {
            if (manager.startEvent(sender, summonArgs) != null)
                Config.sendMessage(sender, "start", manager.getReplacementInformation(0));
        });
    }
}
