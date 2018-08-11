package com.danielcs.mercuryevents.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Event {

    private long id = -1;
    private String name;
    private String description;
    private String pictureUrl;
    private long createdAt;
    private long startDate;
    private long endDate;
    private String location;
    private String organizer;
    private Set<String> participants = new HashSet<>();

    public Event() {
    }

    public Event(long id, String name, String description, String pictureUrl, long createdAt, long startDate,
                 long endDate, String location, String organizer, String participants) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.createdAt = createdAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.organizer = organizer;
        if (!participants.isEmpty()) {
            this.participants = Arrays.stream(participants.split(",")).collect(Collectors.toSet());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Set<String> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<String> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", createdAt=" + createdAt +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", location='" + location + '\'' +
                ", organizer='" + organizer + '\'' +
                ", participants=" + participants +
                '}';
    }
}
