package model;

import model.enums.StatusEnum;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, StatusEnum status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}
