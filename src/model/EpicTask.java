package model;

import model.enums.StatusEnum;

import java.util.ArrayList;

public class EpicTask extends Task {
    ArrayList<Integer> subtaskIds;

    public EpicTask(String title, String description) {
        super(title, description, StatusEnum.NEW);
        this.subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

}
