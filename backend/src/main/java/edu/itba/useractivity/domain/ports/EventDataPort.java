package org.example.domain.ports;

import org.example.domain.models.Event;

import java.util.List;

public interface EventDataPort {
    List<Event> getEventsByUser(String username);
}
