package com.danielcs.mercuryevents.middleware;

import com.danielcs.webserver.http.Request;
import com.danielcs.webserver.http.Responder;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticatorTest {

    private Authenticator authenticator;
    private JwtConsumer jwtConsumer;
    private Request request;
    private Responder responder;
    private JwtClaims claims;

    private ArgumentCaptor<String> valueCaptor;
    private ArgumentCaptor<Integer> errorCodeCaptor;

    @BeforeEach
    void setUp() {
        jwtConsumer = mock(JwtConsumer.class);
        authenticator = new Authenticator(jwtConsumer);
        request = mock(Request.class);
        responder = mock(Responder.class);
        claims = mock(JwtClaims.class);
        valueCaptor = ArgumentCaptor.forClass(String.class);
        errorCodeCaptor = ArgumentCaptor.forClass(Integer.class);
    }

    @Test
    void acceptsCorrectToken() throws Exception {
        NumericDate date = NumericDate.fromMilliseconds(new Date().getTime() + 10000);
        ArgumentCaptor<String> propNameCaptor = ArgumentCaptor.forClass(String.class);
        when(claims.getExpirationTime()).thenReturn(date);
        when(claims.getSubject()).thenReturn("subject");
        when(request.getHeader("Authorization")).thenReturn("Bearer correctToken");
        when(jwtConsumer.processToClaims("correctToken")).thenReturn(claims);
        assertTrue(authenticator.process(request, responder));
        verify(request).setProperty(propNameCaptor.capture(), valueCaptor.capture());
        assertEquals("userId", propNameCaptor.getValue());
        assertEquals("subject", valueCaptor.getValue());
    }

    @Test
    void rejectsExpiredToken() throws Exception {
        NumericDate date = NumericDate.fromMilliseconds(new Date().getTime() - 10000);
        when(claims.getExpirationTime()).thenReturn(date);
        when(request.getHeader("Authorization")).thenReturn("Bearer correctToken");
        when(jwtConsumer.processToClaims("correctToken")).thenReturn(claims);
        assertFalse(authenticator.process(request, responder));
        verify(responder).sendError(errorCodeCaptor.capture(), valueCaptor.capture());
        assertEquals(401, (int)errorCodeCaptor.getValue());
        assertEquals("The token has expired!", valueCaptor.getValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    void rejectsIncorrectToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer incorrectToken");
        when(jwtConsumer.processToClaims("incorrectToken")).thenThrow(InvalidJwtException.class);
        assertFalse(authenticator.process(request, responder));
        verify(responder).sendError(errorCodeCaptor.capture(), valueCaptor.capture());
        assertEquals(401, (int)errorCodeCaptor.getValue());
        assertEquals("Invalid token!", valueCaptor.getValue());
    }

    @Test
    void rejectsMalformedAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("correctToken");
        assertFalse(authenticator.process(request, responder));
        verify(responder).sendError(errorCodeCaptor.capture(), valueCaptor.capture());
        assertEquals(401, (int)errorCodeCaptor.getValue());
        assertEquals("Authorization header must use the bearer scheme!", valueCaptor.getValue());
    }

    @Test
    void rejectsMissingAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        assertFalse(authenticator.process(request, responder));
        verify(responder).sendError(errorCodeCaptor.capture(), valueCaptor.capture());
        assertEquals(401, (int)errorCodeCaptor.getValue());
        assertEquals("Authorization header must be present!", valueCaptor.getValue());
    }
}