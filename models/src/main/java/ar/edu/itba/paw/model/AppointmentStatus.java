package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.exceptions.InvalidFilterException;

import java.util.Arrays;

public enum AppointmentStatus {
    CONFIRMED("confirmed"),
    PENDING("pending"),
    FINISHED("finished"),
    DENIED("denied"),
    CANCELLED("cancelled");

    private final String value;
    public static final String DEFAULT_VALUE = "pending";

    AppointmentStatus(final String value) {
        this.value = value;
    }

    public static AppointmentStatus toEnum(String value) {
        return Arrays.stream(values())
                .filter(s -> s.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(InvalidFilterException::new);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
