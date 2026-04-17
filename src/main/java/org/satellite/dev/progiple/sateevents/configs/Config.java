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
        config = new IConfig(SateEvents.getInstance());
    }

    public void reload() {
        config.reload(SateEvents.getInstance());
    }

    public void sendMessage(CommandSender sender, String id, String... rpl) {
        config.sendMessage(sender, id, rpl);
    }

    public String getString(String id) {
        return config.getString(id);
    }

    public int getFindLocAtt() {
        return config.getInt("findLocationAttempts");
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }
}
