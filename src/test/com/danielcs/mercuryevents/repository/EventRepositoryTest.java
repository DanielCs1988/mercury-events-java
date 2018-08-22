package com.danielcs.mercuryevents.repository;

import com.danielcs.mercuryevents.models.Event;
import com.danielcs.mercuryevents.repository.utils.SQLUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EventRepositoryTest {

    private EventRepository repository;
    private final SQLUtils sqlUtils = new SQLUtils(
            System.getenv("DB_PATH"),
            System.getenv("DB_USER"),
            System.getenv("DB_PASSWORD")
    );

    private final Event eventOne = new Event(
            1, "Grill Party", "Cooking steaks!", "tasty_steak.jpg",
            1533943800000L, 1534777200000L, 1534806000000L,
            "Garden", "Me", "Me,Him,Her"
    );
    private final Event eventTwo = new Event(
            2, "Name", "Description", null,
            1533943800000L, 1534777200000L, 1534806000000L,
            "Location", "Organizer", ""
    );
    private final long now = new Date().getTime();
    private Event newEvent;

    @BeforeEach
    void setUp() {
        repository = new EventRepository();
        repository.setDb(sqlUtils);
        newEvent = new Event(
                -1, "Blizzcon", "Best game cinematics in one place", "some_nerdy_pic.jpeg",
                now, now + 150000, now + 200000, "USA", "Blizzard",
                "Arthas,N'zoth,Sargeras,Saurfang"
        );
    }

    @Test
    void getEvents() {
        List<Event> result = repository.getEvents().stream()
                .sorted(Comparator.comparingLong(Event::getId))
                .collect(Collectors.toList());
        List<Event> expected = Arrays.asList(eventOne, eventTwo);
        assertEquals(expected.toString(), result.toString());
    }

    @Test
    void getEvent() {
        Event result = repository.getEvent(1);
        assertEquals(eventOne.toString(), result.toString());
    }

    @Test
    void createEvent() {
        Event result = repository.createEvent(newEvent);
        repository.deleteEvent(result.getId());
        result.setId(-1);
        assertEquals(newEvent.toString(), result.toString());
    }

    @Test
    void updateEvent() {
        Event event = repository.createEvent(newEvent);
        event.setName("Super Grill Party");
        event.setStartDate(1534770000000L);
        event.setLocation("Anaheim Convention Center");
        Event result = repository.updateEvent(event.getId(), event);
        repository.deleteEvent(event.getId());
        assertEquals(event.toString(), result.toString());
    }

    @Test
    void deleteEvent() {
        Event event = repository.createEvent(newEvent);
        repository.deleteEvent(event.getId());
        Event result = repository.getEvent(event.getId());
        assertNull(result);
    }

    @Test
    void changeParticipation() {
        Event event = repository.createEvent(newEvent);
        Set<String> newParticipants = new HashSet<>(Arrays.asList("Arthas", "N'zoth", "Saurfang", "Sylvanas"));
        event.setParticipants(newParticipants);
        Event result = repository.changeParticipation(event.getId(), newParticipants);
        repository.deleteEvent(event.getId());
        assertEquals(event.toString(), result.toString());
    }
}