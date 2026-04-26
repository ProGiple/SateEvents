package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Args;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.RequiredStage;
import org.satellite.dev.progiple.sateevents.event.SateEventsManager;
import org.satellite.dev.progiple.sateevents.event.realization.IEventManager;
import org.satellite.dev.progiple.sateevents.event.realization.IEventStage;

import java.util.List;

@SubCommand(commandIdentifiers = {"nextStage"}, appliedCommand = "sateevents")
@Permissions("@.nextStage")
@Args(min = 2, max = 2)
public class NextStageSubCommand implements LunaExecutor {
    @Override
    public void invoke(CommandSender sender, String[] strings) {
        IEventManager manager = SateEventsManager.getManager(strings[1]);
        if (manager == null) {
            Config.sendMessage(sender, "eventNotExists", "id-%-" + strings[1]);
            return;
        }

        if (!manager.isActive()) {
            Config.sendMessage(sender, "eventIsInactive", manager.getReplacementInformation(0));
            return;
        }

        IEventStage stage = manager.getLaunched().getStage();
        Class<? extends IEventStage> stageClass = stage.getClass();
        if (stageClass.isAnnotationPresent(RequiredStage.class)) {
            Config.sendMessage(sender, "nextStageError", manager.getReplacementInformation(0));
            return;
        }

        manager.getLaunched().nextStage();
        String[] rpl = manager.getReplacementInformation(2);
        rpl[rpl.length - 1] = "prevStageName-%-" + stage.getName();
        rpl[rpl.length - 2] = "prevStageId-%-" + stage.getId();
        Config.sendMessage(sender, "nextStage", rpl);
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> list) {
        return list.size() == 1 ? Utils.tabCompleterFiltering(SateEventsManager.getActiveManagers()
                .map(IEventManager::getId), list.get(0)) : null;
    }
}
