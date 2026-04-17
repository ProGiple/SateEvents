package org.satellite.dev.progiple.sateevents.event.realization.settings;

public record CoordinateSettings(int minX,
                                 int maxX,
                                 int minY,
                                 int maxY,
                                 int minZ,
                                 int maxZ) implements Settings {
}
