package edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions;

public class InvalidEventTypeException extends RuntimeException {
    public InvalidEventTypeException(String type) {
        super("Invalid event type: " + type);
    }
}