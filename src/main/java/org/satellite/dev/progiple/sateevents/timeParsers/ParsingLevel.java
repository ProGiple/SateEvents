package org.satellite.dev.progiple.sateevents.timeParsers;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ParsingLevel {
    SECONDS(1, 59),
    MINUTES(60, 3599),
    HOURS(3600, 86399),
    DAYS(86400, Long.MAX_VALUE);

    public final long minimalValue;
    public final long maximalValue;

    public static ParsingLevel get(long seconds) {
        for (ParsingLevel value : values()) {
            if (seconds >= value.minimalValue && seconds <= value.maximalValue) {
                return value;
            }
        }

        return null;
    }
}