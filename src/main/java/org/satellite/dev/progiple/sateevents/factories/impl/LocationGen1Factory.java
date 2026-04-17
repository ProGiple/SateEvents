package org.satellite.dev.progiple.sateevents.factories.impl;

import org.satellite.dev.progiple.sateevents.factories.LocationGenFactory;
import org.satellite.dev.progiple.sateevents.factories.storage.FactoryId;
import org.satellite.dev.progiple.sateevents.event.realization.searcher.LocationGen;
import org.satellite.dev.progiple.sateevents.event.realization.searcher.gens.LocationGen1;

@FactoryId("gen1")
public class LocationGen1Factory implements LocationGenFactory {
    @Override
    public LocationGen create() {
        return new LocationGen1();
    }
}
