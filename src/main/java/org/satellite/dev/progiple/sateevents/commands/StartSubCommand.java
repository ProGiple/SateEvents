package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.API.util.utilities.lists.LunaList;
import org.satellite.dev.progiple.sateevents.configs.Config;
import org.satellite.dev.progiple.sateevents.event.SateEventsManager;
import org.satellite.dev.progiple.sateevents.event.realization.IEventManager;

import java.util.List;
import java.util.stream.Collectors;

@SubCommand(commandIdentifiers = {"start"}, appliedCommand = "sateevents")
@Permissions("@.start")
public class StartSubCommand implements LunaExecutor {
    @Override
    public void invoke(CommandSender sender, String[] args) {
        IEventManager manager;
        String[] summonArgs;
        if (args.length == 1) {
            LunaList<IEventManager> managers = SateEventsManager.getManagers()
                    .stream()
                    .filter(m -> !m.isActive())
                    .collect(Collectors.toCollection(LunaList::new));

            manager = managers.randomElement();
            if (manager == null) {
                Config.sendMessage(sender, "eventPoolIsEmpty");
                return;
            }

            summonArgs = null;
        }
        else {
            manager = SateEventsManager.getManager(args[1]);
            if (manager == null) {
                Config.sendMessage(sender, "eventNotExists", "id-%-" + args[1]);
                return;
            }

            if (manager.isActive()) {
                Config.sendMessage(sender, "eventIsActive", manager.getReplacementInformation(0));
                return;
            }

            summonArgs = new String[args.length - 2];
            System.arraycopy(args, 2, summonArgs, 0, args.length - 2);
        }

        if (manager.startEvent(sender, summonArgs) != null)
            Config.sendMessage(sender, "start", manager.getReplacementInformation(0));
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> args) {
        if (args.size() == 1) {
            return Utils.tabCompleterFiltering(SateEventsManager.getManagers()
                    .stream()
                    .filter(m -> !m.isActive())
                    .map(IEventManager::getId), args.get(0));
        }
        else if (args.size() == 2) {
            IEventManager manager = SateEventsManager.getManager(args.get(0));
            if (manager != null && !manager.isActive())
                return manager.tabComplete(commandSender, args);
        }

        return null;
    }
}
