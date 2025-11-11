package edu.itba.useractivity.domain.models.exceptions;

public class InvalidEventTypeException extends RuntimeException {
    public InvalidEventTypeException(String eventName) {
        super("Unknown or invalid event name: " + eventName);
    }
}
