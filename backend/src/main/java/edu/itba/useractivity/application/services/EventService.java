package edu.itba.useractivity.application.services;

import edu.itba.useractivity.application.usecases.GetUserEventsByTypeUseCase;
import edu.itba.useractivity.application.usecases.GetUserEventsUseCase;
import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.ports.inbound.EventInboundPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService implements EventInboundPort {
    private final GetUserEventsUseCase getUserEventsUseCase;
    private final GetUserEventsByTypeUseCase getUserEventsByTypeUseCase;

    @Override
    public List<Event> getUserEvents(String username, int page, int perPage) {
        return getUserEventsUseCase.execute(username, page, perPage);
    }

    @Override
    public List<Event> getUserEventsByType(EventType type, String username, int page, int perPage) {
        return getUserEventsByTypeUseCase.execute(type, username, page, perPage);
    }
}
