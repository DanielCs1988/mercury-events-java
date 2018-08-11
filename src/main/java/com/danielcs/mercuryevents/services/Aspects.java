package com.danielcs.mercuryevents.services;

import com.danielcs.mercuryevents.models.Event;
import com.danielcs.mercuryevents.repository.EventRepository;
import com.danielcs.webserver.core.annotations.Aspect;
import com.danielcs.webserver.core.annotations.AspectType;
import com.danielcs.webserver.http.Request;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;

public class Aspects {

    private final EventRepository eventRepository;

    public Aspects(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Aspect(type = AspectType.INTERCEPTOR)
    public boolean validateJson(Object... args) throws IOException {
        final long currentTime = new Timestamp(System.currentTimeMillis()).getTime();
        Request request = (Request)args[1];
        Event event = (Event)args[2];
        if (
                event.getName() == null || event.getName().isEmpty() ||
                event.getDescription() == null || event.getDescription().isEmpty() ||
                event.getLocation() == null || event.getLocation().isEmpty() ||
                event.getStartDate() < currentTime || event.getEndDate() < currentTime ||
                event.getEndDate() < event.getStartDate()
        ) {
            request.getResponder().sendError(400, "Event data is invalid!");
            return false;
        }
        return true;
    }

    @Aspect(type = AspectType.INTERCEPTOR)
    public boolean authorize(Object... args) throws IOException {
        Request request = (Request)args[1];
        int id = args[2] instanceof Integer ? (int)args[2] : (int)args[3];
        Event event = eventRepository.getEvent(id);
        if (event == null) {
            request.getResponder().sendError(404, "There is no event with id " + id + "!");
            return false;
        }
        if (!event.getOrganizer().equals(request.getProperty("userId"))) {
            String ip = request.getAddress().toString();
            request.getResponder().sendError(401, "Unauthorized modification from " + ip + "!");
            return false;
        }
        return true;
    }
}
