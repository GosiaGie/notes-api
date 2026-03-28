package com.notes_api.user.register.datetime;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
public class DateTimeImpl implements DateTime {

    private final Clock clock;

    @Autowired
    public DateTimeImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Instant getInstant() {
        return Instant.now(clock);
    }

    @Override
    public LocalDateTime getDateTime() {
        return LocalDateTime.now(clock);
    }

    @Override
    public LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, clock.getZone());
    }

    @Override
    public LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), clock.getZone());
    }

}
