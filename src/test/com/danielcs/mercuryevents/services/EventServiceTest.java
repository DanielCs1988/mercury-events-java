package com.danielcs.mercuryevents.services;

import com.danielcs.mercuryevents.models.Event;
import com.danielcs.mercuryevents.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventServiceTest {

    private EventService service;
    private EventRepository eventRepository;
    private final String userId = "Thanos";

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        service = new EventService();
        service.setEventRepository(eventRepository);
    }

    @Test
    void changeParticipationReturnsNullWhenEventNotFound() {
        when(eventRepository.getEvent(128)).thenReturn(null);
        assertNull(service.changeParticipation(128, userId));
    }

    @Test
    void changeParticipationAddsNewParticipant() {
        Event event = new Event();
        Event expected = new Event();
        Set<String> participants = new HashSet<>(Arrays.asList("Dr. Strange", "Deadpool"));
        event.setParticipants(participants);
        participants.add(userId);
        expected.setParticipants(participants);
        when(eventRepository.getEvent(13)).thenReturn(event);
        when(eventRepository.changeParticipation(13, participants)).thenReturn(expected);
        assertEquals(expected, service.changeParticipation(13, userId));
    }

    @Test
    void changeParticipationRemovesExistingParticipant() {
        Event event = new Event();
        Event expected = new Event();
        Set<String> participants = new HashSet<>(Arrays.asList("Dr. Strange", "Deadpool", userId));
        event.setParticipants(participants);
        participants.remove(userId);
        expected.setParticipants(participants);
        when(eventRepository.getEvent(13)).thenReturn(event);
        when(eventRepository.changeParticipation(13, participants)).thenReturn(expected);
        assertEquals(expected, service.changeParticipation(13, userId));
    }
}