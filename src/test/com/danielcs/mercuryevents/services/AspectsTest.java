package com.danielcs.mercuryevents.services;

import com.danielcs.mercuryevents.models.Event;
import com.danielcs.mercuryevents.repository.EventRepository;
import com.danielcs.webserver.http.Request;
import com.danielcs.webserver.http.Responder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AspectsTest {

    private Aspects aspects;
    private EventRepository eventRepository;
    private Request request;
    private Responder responder;
    private ArgumentCaptor<String> valueCaptor;
    private ArgumentCaptor<Integer> errorCodeCaptor;

    private final long now = new Date().getTime();
    private final Event event = new Event(
            13, "Blizzcon", "Best game cinematics in one place", "some_nerdy_pic.jpeg",
            now, now + 150000, now + 200000, "USA", "Blizzard",
            "Arthas,N'zoth,Sargeras,Saurfang"
    );

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        aspects = new Aspects(eventRepository);
        request = mock(Request.class);
        responder = mock(Responder.class);
        when(request.getResponder()).thenReturn(responder);
        valueCaptor = ArgumentCaptor.forClass(String.class);
        errorCodeCaptor = ArgumentCaptor.forClass(Integer.class);
    }

    @Test
    void validateJsonAcceptsValidJson() throws IOException {
        boolean isJsonValid = aspects.validateJson("methodName", request, event);
        assertTrue(isJsonValid);
    }

    @Test
    void validateJsonRejectsNullName() throws IOException {
        event.setName(null);
        boolean isJsonValid = aspects.validateJson("methodName", request, event);
        assertFalse(isJsonValid);
    }

    @Test
    void validateJsonRespondsWithErrorWhenRejecting() throws IOException {
        event.setName(null);
        aspects.validateJson("methodName", request, event);
        verify(responder).sendError(errorCodeCaptor.capture(), valueCaptor.capture());
        assertEquals(400, (int)errorCodeCaptor.getValue());
        assertEquals("Event data is invalid!", valueCaptor.getValue());
    }

    @Test
    void validateJsonRejectsEmptyName() throws IOException {
        event.setName("");
        boolean isJsonValid = aspects.validateJson("methodName", request, event);
        assertFalse(isJsonValid);
    }

    @Test
    void validateJsonRejectsNullDescription() throws IOException {
        event.setDescription(null);
        boolean isJsonValid = aspects.validateJson("methodName", request, event);
        assertFalse(isJsonValid);
    }

    @Test
    void validateJsonRejectsEmptyDescription() throws IOException {
        event.setDescription("");
        boolean isJsonValid = aspects.validateJson("methodName", request, event);
        assertFalse(isJsonValid);
    }

    @Test
    void validateJsonRejectsNullLocation() throws IOException {
        event.setLocation(null);
        boolean isJsonValid = aspects.validateJson("methodName", request, event);
        assertFalse(isJsonValid);
    }

    @Test
    void validateJsonRejectsEmptyLocation() throws IOException {
        event.setLocation("");
        boolean isJsonValid = aspects.validateJson("methodName", request, event);
        assertFalse(isJsonValid);
    }

    @Test
    void validateJsonRejectsInvalidStartDate() throws IOException {
        event.setStartDate(now - 1000);
        boolean isJsonValid = aspects.validateJson("methodName", request, event);
        assertFalse(isJsonValid);
    }

    @Test
    void validateJsonRejectsInvalidEndDate() throws IOException {
        event.setEndDate(now - 1000);
        boolean isJsonValid = aspects.validateJson("methodName", request, event);
        assertFalse(isJsonValid);
    }

    @Test
    void validateJsonRejectsInvalidDatePair() throws IOException {
        event.setEndDate(now + 100000);
        boolean isJsonValid = aspects.validateJson("methodName", request, event);
        assertFalse(isJsonValid);
    }

    @Test
    void authorizeAcceptsValidAttemptWhenThirdParamIsId() throws IOException {
        when(eventRepository.getEvent(13)).thenReturn(event);
        when(request.getProperty("userId")).thenReturn(event.getOrganizer());
        boolean isAuthorized = aspects.authorize("methodName", request, 13);
        assertTrue(isAuthorized);
    }

    @Test
    void authorizeAcceptsValidAttemptWhenFourthParamIsId() throws IOException {
        when(eventRepository.getEvent(13)).thenReturn(event);
        when(request.getProperty("userId")).thenReturn(event.getOrganizer());
        boolean isAuthorized = aspects.authorize("methodName", request, event, 13);
        assertTrue(isAuthorized);
    }

    @Test
    void authorizeRejectsWhenEventNotFound() throws IOException {
        when(eventRepository.getEvent(128)).thenReturn(null);
        boolean isAuthorized = aspects.authorize("methodName", request, 128);
        assertFalse(isAuthorized);
        verify(responder).sendError(errorCodeCaptor.capture(), valueCaptor.capture());
        assertEquals(404, (int)errorCodeCaptor.getValue());
        assertEquals("There is no event with id 128!", valueCaptor.getValue());
    }

    @Test
    void authorizeRejectsInvalidUser() throws IOException {
        InetSocketAddress ip = new InetSocketAddress("ea.games.com", 666);
        when(eventRepository.getEvent(13)).thenReturn(event);
        when(request.getProperty("userId")).thenReturn("EA Games");
        when(request.getAddress()).thenReturn(ip);
        boolean isAuthorized = aspects.authorize("methodName", request, 13);
        assertFalse(isAuthorized);
        verify(responder).sendError(errorCodeCaptor.capture(), valueCaptor.capture());
        assertEquals(401, (int)errorCodeCaptor.getValue());
        assertEquals("Unauthorized modification from " + ip + "!", valueCaptor.getValue());
    }
}