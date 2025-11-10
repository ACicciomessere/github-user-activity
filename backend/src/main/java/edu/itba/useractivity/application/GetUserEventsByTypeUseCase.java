package edu.itba.useractivity.application;

import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.models.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserEventsByTypeUseCase {
    private final GetUserEventsUseCase getUserEventsUseCase;

    public List<Event> execute(EventType eventType, String username) {
        return getUserEventsUseCase.execute(username).stream()
                .filter(e -> eventType.equals(e.getType()))
                .toList();
    }
}
