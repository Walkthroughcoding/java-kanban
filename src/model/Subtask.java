package model;

import java.time.LocalDateTime;
import java.time.Duration;

import model.enums.StatusEnum;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, StatusEnum status, Duration duration, LocalDateTime startTime, int epicId) {
        super(title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}
