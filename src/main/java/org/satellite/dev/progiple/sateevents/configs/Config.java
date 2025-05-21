package org.satellite.dev.progiple.sateevents.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.satellite.dev.progiple.sateevents.SateEvents;

@UtilityClass
public class Config {
    private final IConfig config;
    static {
        config = new IConfig(SateEvents.getINSTANCE());
    }

    public void reload() {
        config.reload(SateEvents.getINSTANCE());
    }

    public void sendMessage(CommandSender sender, String id, String... rpl) {
        config.sendMessage(sender, id, rpl);
    }

    public String getMessage(String id) {
        return ColorManager.color(config.getString("messages." + id));
    }
}
