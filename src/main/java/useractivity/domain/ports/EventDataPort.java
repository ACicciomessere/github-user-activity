package useractivity.domain.ports;

import useractivity.domain.models.Event;

import java.util.List;

public interface EventDataPort {
    List<Event> getEventsByUser(String username);
}
