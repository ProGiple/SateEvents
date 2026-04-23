package org.satellite.dev.progiple.sateevents.timeParsers.impl;

import org.satellite.dev.progiple.sateevents.timeParsers.Parser;
import org.satellite.dev.progiple.sateevents.timeParsers.ParsingLevel;

public class SameParser implements Parser {
    private final String[] format;
    public SameParser(String[] format) {
        this.format = format;
    }

    @Override
    public String parse(long seconds) {
        StringBuilder builder = new StringBuilder();

        ParsingLevel level;
        while (true) {
            level = ParsingLevel.get(seconds);
            if (level == null) break;

            long value = seconds / level.minimalValue;
            seconds -= value * level.minimalValue;

            builder.append(String.format(format[0], value));
            builder.append(format[1]);
        }

        String line = builder.toString();
        if (line.endsWith(format[1])) line = line.substring(0, line.length() - format[1].length());
        return line;
    }
}
