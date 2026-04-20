package org.satellite.dev.progiple.sateevents.factories.storage;

import lombok.experimental.UtilityClass;
import org.satellite.dev.progiple.sateevents.SateEvents;
import org.satellite.dev.progiple.sateevents.factories.Factory;
import org.satellite.dev.progiple.sateevents.factories.impl.*;
import org.satellite.dev.progiple.sateevents.exceptions.FactoryIsNullException;
import org.satellite.dev.progiple.sateevents.exceptions.FactoryMapIsNullException;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Factories {
    private final Map<Class<? extends Factory>, Map<String, Factory>> factories = new HashMap<>();
    static {
        load();
    }

    private void load() {
        register(new LocationGen1Factory());
        register(new LocationGen2Factory());
        register(new WorldEditSchematicFactory());
        register(new RandomSpawnSettingsFactory());
        register(new StaticSpawnSettingsFactory());
    }

    public <F extends Factory> F getFactory(Class<F> clazz, String id) {
        var map = factories.get(clazz);
        if (map == null) {
            throw new FactoryMapIsNullException(clazz, factories.keySet());
        }

        var factory = map.get(id);
        if (factory == null) {
            throw new FactoryIsNullException(id, map.keySet());
        }

        return clazz.cast(factory);
    }

    public void register(Factory factory) {
        var factoryClass = getFactoryClass(factory);

        var map = factories.computeIfAbsent(factoryClass, k -> new HashMap<>());
        map.put(factory.getId(), factory);
    }

    public void unregister(Factory factory) {
        var factoryClass = getFactoryClass(factory);

        var map = factories.get(factoryClass);
        if (map == null) return;

        map.remove(factory.getId(), factory);
    }

    public Class<? extends Factory> getFactoryClass(Factory factory) {
        return getFactoryClass(factory.getClass());
    }

    public <F extends Factory> Class<? extends Factory> getFactoryClass(Class<F> clazz) {
        for (var key : factories.keySet()) {
            if (key.isAssignableFrom(clazz))
                return key;
        }

        return getFactoryClassWithSuper(clazz);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Factory> getFactoryClassWithSuper(Class<? extends Factory> clazz) {
        Class<? extends Factory> factoryClass = Factory.class;
        for (Class<?> anInterface : clazz.getInterfaces()) {
            if (factoryClass.isAssignableFrom(anInterface))
                return (Class<? extends Factory>) anInterface;
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass == null) {
            return getFactoryClassWithSuper(clazz);
        }

        return (Class<? extends Factory>) superClass;
    }
}
