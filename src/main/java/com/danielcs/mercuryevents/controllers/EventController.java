package com.danielcs.mercuryevents.controllers;

import com.danielcs.mercuryevents.models.Event;
import com.danielcs.mercuryevents.services.EventService;
import com.danielcs.webserver.core.annotations.Weave;
import com.danielcs.webserver.http.*;
import com.danielcs.webserver.http.annotations.*;

import java.util.List;

public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @WebRoute(path = "/events")
    public List<Event> getEvents(Request request) {
        return eventService.getEvents();
    }

    @WebRoute(path = "/events/:id")
    public Response<?> getEvent(Request request, int id) {
        Event event = eventService.getEvent(id);
        return event == null ?
                new Response<>(ResponseType.NOT_FOUND, "There is no event with id " + id + "!") :
                new Response<>(ResponseType.OK, event);
    }

    @Weave(aspect = "validateJson")
    @WebRoute(path = "/events", method = Method.POST)
    public Event createEvent(Request request, @Body Event event) {
        return eventService.createEvent(event, request.getProperty("userId"));
    }

    @Weave(aspect = "authorize")
    @WebRoute(path = "/events/:id", method = Method.PUT)
    public Event updateEvent(Request request, @Body Event event, int id) {
        return eventService.updateEvent(id, event);
    }

    @Weave(aspect = "authorize")
    @WebRoute(path = "/events/:id", method = Method.DELETE)
    public Event deleteEvent(Request request, int id) {
        return eventService.deleteEvent(id);
    }

    @WebRoute(path = "/events/:id", method = Method.POST)
    public Response<?> changeEventParticipation(Request request, int id) {
        Event event = eventService.changeParticipation(id, request.getProperty("userId"));
        return event == null ?
                new Response<>(ResponseType.NOT_FOUND, "There is no event with id " + id + "!") :
                new Response<>(ResponseType.OK, event);
    }
}
