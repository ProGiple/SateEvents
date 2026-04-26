package org.satellite.dev.progiple.sateevents.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;

public record EventTime(@Nullable DayOfWeek dayOfWeek, short hour, short minute, short second) implements Temporal, TemporalAdjuster {
    private static final TemporalUnit[] SUPPORTED_UNITS = {
            ChronoUnit.SECONDS,
            ChronoUnit.MINUTES,
            ChronoUnit.HOURS,
            ChronoUnit.DAYS,
            ChronoUnit.WEEKS
    };

    private static final TemporalField[] SUPPORTED_FIELDS = {
            ChronoField.SECOND_OF_MINUTE,
            ChronoField.MINUTE_OF_HOUR,
            ChronoField.HOUR_OF_DAY,
            ChronoField.DAY_OF_WEEK,
            ChronoField.ALIGNED_WEEK_OF_MONTH
    };

    public static EventTime of(LocalDateTime dateTime) {
        return new EventTime(
                dateTime.getDayOfWeek(),
                (short) dateTime.getHour(),
                (short) dateTime.getMinute(),
                (short) dateTime.getSecond()
        );
    }

    public static EventTime of(LocalTime time) {
        return new EventTime(
                null,
                (short) time.getHour(),
                (short) time.getMinute(),
                (short) time.getSecond()
        );
    }

    public long getFullSeconds() {
        return hour * 3600 + minute * 60 + second;
    }

    public LocalTime toLocalTime() {
        return LocalTime.of(hour, minute, second);
    }

    public LocalDateTime toLocalDateTime(LocalDate date) {
        return LocalDateTime.of(date, toLocalTime());
    }

    /**
     * Возвращает следующий момент времени после указанной даты
     */
    public LocalDateTime nextAfter(LocalDateTime source) {
        long seconds = getSecondsFromNow(source);
        return source.plusSeconds(seconds);
    }

    public long getSecondsFromNow(LocalDateTime source) {
        long targetSeconds = getFullSeconds();
        int sourceSeconds = source.toLocalTime().toSecondOfDay();

        long timeDiff = targetSeconds - sourceSeconds;
        if (dayOfWeek == null) {
            if (timeDiff > 0) {
                return timeDiff;
            } else {
                return (24 * 3600) + timeDiff;
            }
        } else {
            DayOfWeek sourceDay = source.getDayOfWeek();
            int targetDayValue = dayOfWeek.getValue();
            int sourceDayValue = sourceDay.getValue();

            int daysUntil = targetDayValue - sourceDayValue;
            if (daysUntil > 0) {
                return daysUntil * 24 * 3600L + timeDiff;
            } else if (daysUntil == 0) {
                if (timeDiff > 0) {
                    return timeDiff;
                } else if (timeDiff == 0) {
                    return 0;
                } else {
                    return 7 * 24 * 3600 + timeDiff;
                }
            } else {
                return (7 + daysUntil) * 24 * 3600L + timeDiff;
            }
        }
    }

    public boolean isNow(LocalDateTime source) {
        boolean time = source.getHour() == this.hour &&
                source.getMinute() == this.minute &&
                source.getSecond() == this.second;
        if (dayOfWeek == null) return time;
        else return time && source.getDayOfWeek() == this.dayOfWeek;
    }

    @Override
    public boolean isSupported(TemporalField field) {
        if (field == null) return false;
        for (TemporalField f : SUPPORTED_FIELDS) {
            if (f.equals(field)) return true;
        }
        return false;
    }

    @Override
    public boolean isSupported(TemporalUnit unit) {
        if (unit == null) return false;
        for (TemporalUnit u : SUPPORTED_UNITS) {
            if (u.equals(unit)) return true;
        }
        return false;
    }

    @Override
    public long getLong(TemporalField field) {
        if (field == ChronoField.SECOND_OF_MINUTE) return second;
        if (field == ChronoField.MINUTE_OF_HOUR) return minute;
        if (field == ChronoField.HOUR_OF_DAY) return hour;
        if (field == ChronoField.DAY_OF_WEEK) {
            return dayOfWeek != null ? dayOfWeek.getValue() : -1;
        }
        throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        EventTime end = (EventTime) endExclusive;
        long secondsDiff = end.getFullSeconds() - this.getFullSeconds();

        if (dayOfWeek != null && end.dayOfWeek != null) {
            int daysDiff = end.dayOfWeek.getValue() - this.dayOfWeek.getValue();
            secondsDiff += daysDiff * 86400L;
        }

        if (unit == ChronoUnit.SECONDS) return secondsDiff;
        else if (unit == ChronoUnit.MINUTES) return secondsDiff / 60;
        else if (unit == ChronoUnit.HOURS) return secondsDiff / 3600;
        else if (unit == ChronoUnit.DAYS) return secondsDiff / 86400;
        else if (unit == ChronoUnit.WEEKS) return secondsDiff / 604800;
        else throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    @Override
    public Temporal with(TemporalField field, long newValue) {
        if (field == ChronoField.SECOND_OF_MINUTE) {
            return new EventTime(dayOfWeek, hour, minute, (short) newValue);
        }
        if (field == ChronoField.MINUTE_OF_HOUR) {
            return new EventTime(dayOfWeek, hour, (short) newValue, second);
        }
        if (field == ChronoField.HOUR_OF_DAY) {
            return new EventTime(dayOfWeek, (short) newValue, minute, second);
        }
        if (field == ChronoField.DAY_OF_WEEK) {
            return new EventTime(DayOfWeek.of((int) newValue), hour, minute, second);
        }
        throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
    }

    @Override
    public Temporal plus(long amountToAdd, TemporalUnit unit) {
        long totalSeconds = getFullSeconds();

        if (unit == ChronoUnit.SECONDS) totalSeconds += amountToAdd;
        else if (unit == ChronoUnit.MINUTES) totalSeconds += amountToAdd * 60;
        else if (unit == ChronoUnit.HOURS) totalSeconds += amountToAdd * 3600;
        else if (unit == ChronoUnit.DAYS) totalSeconds += amountToAdd * 86400;
        else if (unit == ChronoUnit.WEEKS) totalSeconds += amountToAdd * 604800;
        else throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);

        long newHour = (totalSeconds / 3600) % 24;
        long newMinute = (totalSeconds % 3600) / 60;
        long newSecond = totalSeconds % 60;

        long daysShift = totalSeconds / 86400;
        DayOfWeek newDay = null;
        if (dayOfWeek != null) {
            long newDayValue = (dayOfWeek.getValue() - 1 + daysShift) % 7 + 1;
            newDay = DayOfWeek.of((int) newDayValue);
        }

        return new EventTime(
                newDay,
                (short) newHour,
                (short) newMinute,
                (short) newSecond
        );
    }

    @Override
    public Temporal minus(long amountToSubtract, TemporalUnit unit) {
        return plus(-amountToSubtract, unit);
    }

    @Override
    public Temporal adjustInto(Temporal temporal) {
        if (temporal instanceof LocalDateTime ldt) {
            LocalDateTime adjusted = ldt
                    .withHour(hour)
                    .withMinute(minute)
                    .withSecond(second)
                    .withNano(0);

            if (dayOfWeek != null) {
                adjusted = adjusted.with(TemporalAdjusters.nextOrSame(dayOfWeek));
                if (adjusted.getHour() > hour ||
                        (adjusted.getHour() == hour && adjusted.getMinute() > minute) ||
                        (adjusted.getHour() == hour && adjusted.getMinute() == minute && adjusted.getSecond() > second)) {
                    adjusted = adjusted.with(TemporalAdjusters.next(dayOfWeek));
                }
                adjusted = adjusted.withHour(hour).withMinute(minute).withSecond(second);
            }

            return adjusted;
        }

        if (temporal instanceof LocalTime lt) {
            return lt.withHour(hour).withMinute(minute).withSecond(second);
        }

        throw new DateTimeException("Cannot adjust " + temporal.getClass().getSimpleName() + " to EventTime");
    }

    public EventTime withDayOfWeek(DayOfWeek newDay) {
        return new EventTime(newDay, hour, minute, second);
    }

    public EventTime withTime(LocalTime time) {
        return new EventTime(dayOfWeek, (short) time.getHour(), (short) time.getMinute(), (short) time.getSecond());
    }

    public boolean isAfter(EventTime other) {
        if (this.dayOfWeek != null && other.dayOfWeek != null) {
            if (this.dayOfWeek != other.dayOfWeek) {
                return this.dayOfWeek.getValue() > other.dayOfWeek.getValue();
            }
        }
        return this.getFullSeconds() > other.getFullSeconds();
    }

    public boolean isBefore(EventTime other) {
        if (this.dayOfWeek != null && other.dayOfWeek != null) {
            if (this.dayOfWeek != other.dayOfWeek) {
                return this.dayOfWeek.getValue() < other.dayOfWeek.getValue();
            }
        }
        return this.getFullSeconds() < other.getFullSeconds();
    }

    @Override
    public @NotNull String toString() {
        if (dayOfWeek == null) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
        return String.format("%s:%02d:%02d:%02d", dayOfWeek, hour, minute, second);
    }

    public String format(DateTimeFormatter formatter) {
        return formatter.format(toLocalTime());
    }
}