package com.notes_api.user.register.datetime;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateTimeImpl implements DateTime {

    private Clock clock;

    @Autowired
    public DateTimeImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public LocalDate getDate() {
        return LocalDate.now();
    }

    @Override
    public LocalDateTime getDateTime() {
        return LocalDateTime.now();
    }
}
