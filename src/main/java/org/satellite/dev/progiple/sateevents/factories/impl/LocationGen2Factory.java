package org.satellite.dev.progiple.sateevents.factories.impl;

import org.satellite.dev.progiple.sateevents.event.realization.searcher.LocationGen;
import org.satellite.dev.progiple.sateevents.event.realization.searcher.gens.LocationGen2;
import org.satellite.dev.progiple.sateevents.factories.LocationGenFactory;
import org.satellite.dev.progiple.sateevents.factories.storage.FactoryId;

@FactoryId("gen2")
public class LocationGen2Factory implements LocationGenFactory {
    @Override
    public LocationGen create() {
        return new LocationGen2();
    }
}
