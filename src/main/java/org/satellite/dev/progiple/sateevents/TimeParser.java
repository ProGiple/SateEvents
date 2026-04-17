package org.satellite.dev.progiple.sateevents;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateevents.event.EventTime;

import java.time.DayOfWeek;

@Getter @Setter
public class TimeParser {
    private String[] format = {"%02d", ":"};

    public String parseTime(long seconds) {
        StringBuilder builder = new StringBuilder();

        Level level;
        while (true) {
            level = Level.get(seconds);
            if (level == null) break;

            long value = seconds / level.minimalValue;
            seconds -= value * level.minimalValue;

            builder.append(String.format(format[0], value)).append(format[1]);
        }

        return builder.toString();
    }

    public EventTime parseTime(String stringTime) {
        short[] time = {0, 0, 0};
        DayOfWeek day = null;

        String[] split = stringTime.split(" ");
        for (String s : split) {
            var tempDay = Utils.getEnumValue(DayOfWeek.class, s);
            if (tempDay != null) {
                day = tempDay;
                continue;
            }

            char let = s.charAt(s.length() - 1);
            int index = let == 's' ? 0 : let == 'm' ? 1 : 2;
            time[index] = (short) LunaMath.toInt(s.substring(0, s.length() - 1));
        }

        return new EventTime(day, time[2], time[1], time[0]);
    };

    @RequiredArgsConstructor
    public enum Level {
        SECONDS(1, 59),
        MINUTES(60, 3599),
        HOURS(3600, 86399),
        DAYS(86400, Long.MAX_VALUE);

        private final long minimalValue;
        private final long maximalValue;

        public static Level get(long seconds) {
            for (Level value : values()) {
                if (seconds >= value.minimalValue && seconds <= value.maximalValue) {
                    return value;
                }
            }

            return null;
        }
    }
}
