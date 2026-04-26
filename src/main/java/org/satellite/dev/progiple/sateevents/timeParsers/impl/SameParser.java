package org.satellite.dev.progiple.sateevents.timeParsers.impl;

import org.satellite.dev.progiple.sateevents.timeParsers.Parser;

public class SameParser implements Parser {
    private final String format;
    public SameParser(String format) {
        this.format = format;
    }

    @Override
    public String parse(long seconds) {
        StringBuilder builder = new StringBuilder();

        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (days > 0)
            append(builder, days, true);
        if (hours > 0 || days > 0)
            append(builder, hours, true);
        append(builder, minutes, true);
        append(builder, secs, false);

        return builder.toString();
    }

    protected void append(StringBuilder builder, long val, boolean addDelim) {
        if (val < 10) builder.append('0');
        builder.append(val);
        if (addDelim) builder.append(format);
    }
}
