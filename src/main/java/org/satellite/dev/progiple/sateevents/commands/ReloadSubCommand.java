package org.satellite.dev.progiple.sateevents.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.satellite.dev.progiple.sateevents.configs.Config;

@SubCommand(commandIdentifiers = {"reload"}, appliedCommand = "sateevents")
@Check(permissions = "sateevents.reload", flags = {})
public class ReloadSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] strings) {
        Config.reload();
        Config.sendMessage(sender, "reload");
    }
}
