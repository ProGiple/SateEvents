package org.satellite.dev.progiple.sateevents.event.realization.settings;

import java.util.Set;

public record ListSettings<E>(Set<E> list, FilterType filter) implements Settings {
    public boolean isValid(E e) {
        return filter.isValid(list, e);
    }
}
