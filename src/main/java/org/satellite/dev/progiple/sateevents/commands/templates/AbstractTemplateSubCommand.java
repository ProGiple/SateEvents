package org.satellite.dev.progiple.sateevents.commands.templates;

import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.satellite.dev.progiple.sateevents.event.realization.IEventManager;

import java.util.Objects;

public abstract class AbstractTemplateSubCommand implements LunaExecutor {
    protected final IEventManager manager;
    public AbstractTemplateSubCommand(IEventManager manager) {
        this.manager = Objects.requireNonNull(manager);
    }
}
