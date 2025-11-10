package edu.itba.useractivity.infrastructure.adapters.driving;

import edu.itba.useractivity.application.GetUserEventsByTypeUseCase;
import edu.itba.useractivity.application.GetUserEventsUseCase;
import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.models.EventType;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {

    private final GetUserEventsUseCase getUserEventsUseCase;
    private final GetUserEventsByTypeUseCase getUserEventsByTypeUseCase;

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Event>> getEvents(@PathVariable("username") String username) {
        List<Event> events = getUserEventsUseCase.execute(username);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventType}/user/{username}")
    public ResponseEntity<List<Event>> getEventsByType(
            @PathVariable("eventType") String eventType,
            @PathVariable("username") String username
    ) {
        EventType type = EventType.fromEventName(eventType);
        if (type == null) {
            throw new IllegalArgumentException("Invalid event type: " + eventType);
        }
        List<Event> events = getUserEventsByTypeUseCase.execute(type, username);
        return ResponseEntity.ok(events);
    }
}

