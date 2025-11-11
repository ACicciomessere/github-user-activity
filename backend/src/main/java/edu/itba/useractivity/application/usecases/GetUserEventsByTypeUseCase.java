package edu.itba.useractivity.application.usecases;

import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.enums.EventType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetUserEventsByTypeUseCase {
    private final GetUserEventsUseCase getUserEventsUseCase;

    public List<Event> execute(EventType eventType, String username, int page, int perPage) {
        return getUserEventsUseCase.execute(username, page, perPage).stream()
                .filter(e -> eventType.equals(e.getType()))
                .toList();
    }
}
