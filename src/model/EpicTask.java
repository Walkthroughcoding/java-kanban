package model;

import model.enums.StatusEnum;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<Subtask> subtasks; // Заменены индикаторы на обьекты с подзадачами

    public EpicTask(String title, String description) {
        super(title, description, StatusEnum.NEW);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

}
