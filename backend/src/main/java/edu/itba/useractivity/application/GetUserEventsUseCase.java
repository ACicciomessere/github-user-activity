package edu.itba.useractivity.application;

import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.ports.outbound.EventDataPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserEventsUseCase {

    private final EventDataPort eventDataPort;

    public List<Event> execute(String username, int page, int perPage) {
        return eventDataPort.getEventsByUser(username, page, perPage);
    }
}
