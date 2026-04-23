package org.satellite.dev.progiple.sateevents.timeParsers.impl;

import org.satellite.dev.progiple.sateevents.timeParsers.Parser;

public class NamedParser implements Parser {
    private final String[] format;
    public NamedParser(String[] format) {
        this.format = format;
    }

    @Override
    public String parse(long seconds) {
        if (seconds <= 0) return "0" + format[3];

        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append(format[0]).append(" ");
        }
        if (hours > 0) {
            result.append(hours).append(format[1]).append(" ");
        }
        if (minutes > 0) {
            result.append(minutes).append(format[2]).append(" ");
        }
        if (secs > 0) {
            result.append(secs).append(format[3]).append(" ");
        }

        return result.toString().trim();
    }
}
