package com.github.hm1rafael.clipping.entities;

import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.LocalDate;

@UtilityClass
class HearingDateCalculator {
    private final int OFFSET_FROM_SATURDAY = 2;
    private final int OFFSET_FROM_SUNDAY = 1;
    public final int DEFAULT_OFFSET = 3;

    public LocalDate calculateNextBusinessDate(LocalDate date) {
        return calculateNextBusinessDate(date, DEFAULT_OFFSET);
    }

    private LocalDate calculateNextBusinessDate(LocalDate date, Integer offset) {
        if (offset <= 0) {
            return date;
        }
        LocalDate localDate = date.plusDays(offset);

        if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return calculateNextBusinessDate(localDate, OFFSET_FROM_SATURDAY);
        } else if (localDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return calculateNextBusinessDate(localDate, OFFSET_FROM_SUNDAY);
        }
        return localDate;

    }

}
