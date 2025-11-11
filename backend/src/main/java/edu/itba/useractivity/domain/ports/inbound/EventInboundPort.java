package edu.itba.useractivity.domain.ports.inbound;

import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.domain.models.Event;

import java.util.List;

public interface EventInboundPort {
    List<Event> getUserEvents(String username, int page, int perPage);
    List<Event> getUserEventsByType(EventType type, String username, int page, int perPage);
}
