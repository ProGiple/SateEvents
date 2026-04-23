package org.satellite.dev.progiple.sateevents.timeParsers;

import lombok.Getter;
import lombok.Setter;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateevents.event.EventTime;

import java.time.DayOfWeek;

@Getter @Setter
public class TimeParser {
    public String parseTime(long seconds, Parser parser) {
        return parser.parse(seconds);
    }

    public String parseTime(long seconds) {
        return parseTime(seconds, ParserStorage.getDefaultParser());
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
}
