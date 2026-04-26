package org.satellite.dev.progiple.sateevents.factories;

import org.satellite.dev.progiple.sateevents.factories.storage.Factories;
import org.satellite.dev.progiple.sateevents.factories.storage.FactoryId;

public interface Factory {
    default Class<? extends Factory> getFactoryClass() {
        return Factories.getFactoryProvider(this);
    }

    default String getId() {
        FactoryId factoryId = this.getClass().getAnnotation(FactoryId.class);
        if (factoryId == null)
            throw new RuntimeException("No FactoryId annotation present in factory class " + this.getClass().getName());
        return factoryId.value();
    }
}
