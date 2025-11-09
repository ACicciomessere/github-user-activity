package edu.itba.useractivity.infrastructure.adapters.driving;

import lombok.AllArgsConstructor;
import org.example.application.GetUserEventsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.domain.models.Event;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {
    private final GetUserEventsUseCase getUserEventsUseCase;

    @GetMapping("/user/{username}")
    public List<Event> getEvents(String username) {
        return getUserEventsUseCase.execute(username);
    }
}
