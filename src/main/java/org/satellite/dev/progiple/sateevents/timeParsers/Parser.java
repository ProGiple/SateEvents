package org.satellite.dev.progiple.sateevents.timeParsers;

import org.satellite.dev.progiple.sateevents.event.EventTime;

import java.time.LocalDateTime;

public interface Parser {
    String parse(long seconds);
    default String parse(EventTime eventTime, LocalDateTime time) {
        return parse(eventTime.getSecondsFromNow(time));
    }
    default String parse(EventTime eventTime) {
        return parse(eventTime, LocalDateTime.now());
    }
}
