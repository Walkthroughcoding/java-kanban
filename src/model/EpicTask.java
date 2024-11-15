package model;

import model.enums.StatusEnum;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private List<Subtask> subtasks; // Заменены индикаторы на обьекты с подзадачами

    public EpicTask(String title, String description) {
        super(title, description, StatusEnum.NEW);
        this.subtasks = new ArrayList<>();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

}
