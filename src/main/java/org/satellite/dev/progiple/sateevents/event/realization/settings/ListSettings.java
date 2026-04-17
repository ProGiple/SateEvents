package org.satellite.dev.progiple.sateevents.event.realization.settings;

import java.util.Collection;

public record ListSettings<E>(Collection<E> list, FilterType filter) implements Settings {
    public boolean isValid(E e) {
        return filter.isValid(list, e);
    }
}
