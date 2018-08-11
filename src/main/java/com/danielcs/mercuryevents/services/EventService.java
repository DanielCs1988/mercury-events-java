package com.danielcs.mercuryevents.services;

import com.danielcs.mercuryevents.models.Event;
import com.danielcs.mercuryevents.repository.EventRepository;
import com.danielcs.webserver.core.annotations.Dependency;
import com.danielcs.webserver.core.annotations.InjectionPoint;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Dependency
public class EventService {

    private EventRepository eventRepository;

    @InjectionPoint
    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.getEvents();
    }

    public Event getEvent(long id) {
        return eventRepository.getEvent(id);
    }

    public Event createEvent(Event event, String userId) {
        final long currentTime = new Timestamp(System.currentTimeMillis()).getTime();
        event.setCreatedAt(currentTime);
        event.setOrganizer(userId);
        return eventRepository.createEvent(event);
    }

    public Event updateEvent(long id, Event event) {
        return eventRepository.updateEvent(id, event);
    }

    public Event deleteEvent(long id) {
        return eventRepository.deleteEvent(id);
    }

    public Event changeParticipation(long id, String userId) {
        Event event = eventRepository.getEvent(id);
        if (event == null) { return null; }
        Set<String> participants = event.getParticipants();
        if (participants.contains(userId)) {
            participants.remove(userId);
        } else {
            participants.add(userId);
        }
        return eventRepository.changeParticipation(id, participants);
    }
}
