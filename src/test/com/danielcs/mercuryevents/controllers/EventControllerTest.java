package com.danielcs.mercuryevents.controllers;

import com.danielcs.mercuryevents.models.Event;
import com.danielcs.mercuryevents.services.EventService;
import com.danielcs.webserver.http.Request;
import com.danielcs.webserver.http.Response;
import com.danielcs.webserver.http.ResponseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventControllerTest {

    private EventController controller;
    private EventService eventService;
    private Request request;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        controller = new EventController(eventService);
        request = mock(Request.class);
        when(request.getProperty("userId")).thenReturn("user");
    }

    @Test
    void getEventReturnsExistingEvents() {
        Event event = new Event();
        when(eventService.getEvent(13)).thenReturn(event);
        Response<Event> expected = new Response<>(ResponseType.OK, event);
        Response<?> result = controller.getEvent(request, 13);
        assertEquals(expected.getType(), result.getType());
        assertEquals(expected.getContent(), result.getContent());
    }

    @Test
    void getEventReturnsErrorWhenEventNotFound() {
        when(eventService.getEvent(128)).thenReturn(null);
        Response<String> expected = new Response<>(ResponseType.NOT_FOUND, "There is no event with id 128!");
        Response<?> result = controller.getEvent(request, 128);
        assertEquals(expected.getType(), result.getType());
        assertEquals(expected.getContent(), result.getContent());
    }

    @Test
    void changeEventParticipationReturnsExistingEvent() {
        Event event = new Event();
        when(eventService.changeParticipation(13, "user")).thenReturn(event);
        Response<Event> expected = new Response<>(ResponseType.OK, event);
        Response<?> result = controller.changeEventParticipation(request, 13);
        assertEquals(expected.getType(), result.getType());
        assertEquals(expected.getContent(), result.getContent());
    }

    @Test
    void changeEventParticipationReturnsErrorWhenEventNotFound() {
        when(eventService.changeParticipation(128, "user")).thenReturn(null);
        Response<String> expected = new Response<>(ResponseType.NOT_FOUND, "There is no event with id 128!");
        Response<?> result = controller.getEvent(request, 128);
        assertEquals(expected.getType(), result.getType());
        assertEquals(expected.getContent(), result.getContent());
    }
}