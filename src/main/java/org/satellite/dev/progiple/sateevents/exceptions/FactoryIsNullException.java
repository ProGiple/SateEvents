package org.satellite.dev.progiple.sateevents.exceptions;

import java.util.Set;

public class FactoryIsNullException extends RuntimeException {
    public FactoryIsNullException(String id, Set<String> classes) {
        super(String.format("Factory %s doesn't exist! The following factories are available: %s",
                id, String.join(", ", classes)));
    }
}
