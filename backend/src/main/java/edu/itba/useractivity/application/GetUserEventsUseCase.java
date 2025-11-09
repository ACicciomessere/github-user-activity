package edu.itba.useractivity.application;

import lombok.AllArgsConstructor;
import org.example.domain.ports.EventDataPort;
import org.example.domain.models.Event;

import java.util.List;

@AllArgsConstructor
public class GetUserEventsUseCase {
    private final EventDataPort eventDataPort;

    public List<Event> execute(String username) {
        return eventDataPort.getEventsByUser(username);
    }
}
