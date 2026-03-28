package com.notes_api.user.register.datetime;

import java.time.Instant;
import java.time.LocalDateTime;

public interface DateTime {

    Instant getInstant();

    LocalDateTime getDateTime();

    LocalDateTime toLocalDateTime(Instant instant);

    LocalDateTime toLocalDateTime(long timestamp);

}