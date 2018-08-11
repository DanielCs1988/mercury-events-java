package com.danielcs.mercuryevents.repository;

import com.danielcs.mercuryevents.models.Event;
import com.danielcs.mercuryevents.repository.utils.ModelAssembler;
import com.danielcs.mercuryevents.repository.utils.SQLUtils;
import com.danielcs.webserver.core.annotations.Dependency;
import com.danielcs.webserver.core.annotations.InjectionPoint;

import java.util.List;
import java.util.Set;

@Dependency
public class EventRepository implements EventDAO {

    private SQLUtils db;

    @InjectionPoint
    public void setDb(SQLUtils db) {
        this.db = db;
    }

    private ModelAssembler<Event> assembler = rs -> new Event(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("pictureUrl"),
            rs.getLong("createdAt"),
            rs.getLong("startDate"),
            rs.getLong("endDate"),
            rs.getString("location"),
            rs.getString("organizer"),
            rs.getString("participants")
    );

    @Override
    public List<Event> getEvents() {
        return db.fetchAll("SELECT * FROM events;", assembler);
    }

    @Override
    public Event getEvent(long id) {
        return db.fetchOne("SELECT * FROM events WHERE id = ?;", assembler, id);
    }

    @Override
    public Event createEvent(Event event) {
        final String participants = String.join(",", event.getParticipants());
        return db.fetchOne(
                "INSERT INTO events (name, description, pictureUrl, createdAt, startDate, endDate," +
                            "location, organizer, participants) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING *;",
                assembler,
                event.getName(), event.getDescription(), event.getPictureUrl(), event.getCreatedAt(),
                event.getStartDate(), event.getEndDate(), event.getLocation(), event.getOrganizer(), participants
        );
    }

    @Override
    public Event updateEvent(long id, Event event) {
        return db.fetchOne(
                "UPDATE events SET name = ?, description = ?, pictureUrl = ?, startDate = ?," +
                           "endDate = ?, location = ? WHERE id = ? RETURNING *;",
                assembler,
                event.getName(), event.getDescription(), event.getPictureUrl(), event.getStartDate(),
                event.getEndDate(), event.getLocation(), id
        );
    }

    @Override
    public Event deleteEvent(long id) {
        return db.fetchOne("DELETE FROM events WHERE id = ? RETURNING *;", assembler, id);
    }

    @Override
    public Event changeParticipation(long id, Set<String> participants) {
        final String prt = String.join(",", participants);
        return db.fetchOne(
                "UPDATE events SET participants = ? WHERE id = ? RETURNING *;",
                assembler, prt, id
        );
    }
}
