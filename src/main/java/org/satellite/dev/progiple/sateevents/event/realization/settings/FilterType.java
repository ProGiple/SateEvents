package org.satellite.dev.progiple.sateevents.event.realization.settings;

import java.util.Collection;

public enum FilterType {
    BLACKLIST {
        @Override
        public <E> boolean isValid(Collection<E> collection, E element) {
            return !collection.contains(element);
        }
    },
    WHITELIST {
        @Override
        public <E> boolean isValid(Collection<E> collection, E element) {
            return collection.contains(element);
        }
    };

    public abstract <E> boolean isValid(Collection<E> collection, E element);
}
