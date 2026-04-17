package org.satellite.dev.progiple.sateevents.exceptions;

import org.satellite.dev.progiple.sateevents.factories.Factory;

import java.util.Set;

public class FactoryMapIsNullException extends RuntimeException {
    public FactoryMapIsNullException(Class<?> clazz, Set<Class<? extends Factory>> classes) {
        super(String.format("There is no factory set for this class %s! Available sets: %s",
                clazz.getSimpleName(),
                String.join(", ", classes.stream().map(Class::getSimpleName).toList())));
    }
}
