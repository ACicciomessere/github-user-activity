package edu.itba.useractivity.application.usecases;

import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.ports.outbound.EventOutboundPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserEventsUseCase {

    private final EventOutboundPort eventDataPort;

    public List<Event> execute(String username, int page, int perPage) {
        return eventDataPort.getEventsByUser(username, page, perPage);
    }
}
