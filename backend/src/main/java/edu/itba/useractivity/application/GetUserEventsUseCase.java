package edu.itba.useractivity.application;

import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.ports.outbound.EventDataPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserEventsUseCase {

    private final EventDataPort eventDataPort;

    public GetUserEventsUseCase(EventDataPort eventDataPort) {
        this.eventDataPort = eventDataPort;
    }

    public List<Event> execute(String username) {
        return eventDataPort.getEventsByUser(username);
    }
}
