package edu.itba.useractivity.infrastructure.adapters.driving;

import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.ports.inbound.EventInboundPort;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {

    private final EventInboundPort eventInboundPort;

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Event>> getEvents(
            @PathVariable("username") String username,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "30") int perPage
    ) {
        List<Event> events = eventInboundPort.getUserEvents(username, page, perPage);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventType}/user/{username}")
    public ResponseEntity<List<Event>> getEventsByType(
            @PathVariable("eventType") String eventType,
            @PathVariable("username") String username,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "30") int perPage
    ) {
        EventType type = EventType.fromEventName(eventType);
        if (type == null) {
            throw new IllegalArgumentException("Invalid event type: " + eventType);
        }
        List<Event> events = eventInboundPort.getUserEventsByType(type, username, page, perPage);
        return ResponseEntity.ok(events);
    }
}

