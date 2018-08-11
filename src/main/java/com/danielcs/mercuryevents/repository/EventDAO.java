package com.danielcs.mercuryevents.repository;

import com.danielcs.mercuryevents.models.Event;

import java.util.List;
import java.util.Set;

public interface EventDAO {
    List<Event> getEvents();
    Event getEvent(long id);
    Event createEvent(Event event);
    Event updateEvent(long id, Event event);
    Event deleteEvent(long id);
    Event changeParticipation(long id, Set<String> participants);
}
