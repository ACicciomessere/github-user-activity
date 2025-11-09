package edu.itba.useractivity.domain.ports;


import edu.itba.useractivity.domain.models.Event;

import java.util.List;

public interface EventDataPort {
    List<Event> getEventsByUser(String username);
}
