package edu.itba.useractivity.infrastructure.adapters.driving;

import edu.itba.useractivity.application.GetUserEventsUseCase;
import edu.itba.useractivity.domain.models.Event;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {
    private final GetUserEventsUseCase getUserEventsUseCase;

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Event>> getEvents(@PathVariable("username") String username) {
        List<Event> events = getUserEventsUseCase.execute(username);
        return ResponseEntity.ok(events);
    }
}
