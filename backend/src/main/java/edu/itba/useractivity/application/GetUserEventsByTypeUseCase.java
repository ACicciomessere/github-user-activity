package edu.itba.useractivity.application;

import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.models.EventType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserEventsByTypeUseCase {
    private final GetUserEventsUseCase getUserEventsUseCase;

    public GetUserEventsByTypeUseCase(GetUserEventsUseCase getUserEventsUseCase) {
        this.getUserEventsUseCase = getUserEventsUseCase;
    }

    public List<Event> execute(EventType eventType, String username) {
        return getUserEventsUseCase.execute(username).stream()
                .filter(e -> eventType.equals(e.getType()))
                .toList();
    }
}
