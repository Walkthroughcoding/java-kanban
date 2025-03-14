package model;

import model.enums.StatusEnum;

import java.time.LocalDateTime;
import java.time.Duration;

public class Task {

    private String title;
    private String description;
    private StatusEnum status;
    private int id;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String title, String description, StatusEnum status, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return (startTime != null && duration != null) ? startTime.plus(duration) : null;
    }

    public Task() {
        this("No Title", "No Description", StatusEnum.NEW, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return id + "," + title + "," + description + "," + status + ","
                + (startTime != null ? startTime.toString() : "null") + ","
                + (duration != null ? duration.toMinutes() : "null");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Task fromString(String line) {
        String[] fields = line.split(",");
        return new Task(fields[1], fields[2],
                StatusEnum.valueOf(fields[3]), // Передаём status в конструктор
                fields[5].equals("null") ? null : Duration.ofMinutes(Long.parseLong(fields[5])),
                fields[4].equals("null") ? null : LocalDateTime.parse(fields[4])
        );
    }
}
