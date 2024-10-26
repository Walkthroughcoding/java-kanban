package manager;

import model.Task;
import model.EpicTask;
import model.Subtask;

import java.util.HashMap;

public class TaskManager {
    private int currentId = 1;
    private HashMap<Integer, Task> taskData = new HashMap<>();
    private HashMap<Integer, EpicTask> EpicData = new HashMap<>();
    private HashMap<Integer, Subtask> SubtaskData = new HashMap<>();

    public int generateId() {
        return currentId++;
    }

    public void addTask(Task task) {
        int id = generateId();
        task.setId(id);
        taskData.put(id,task);
    }

    public void addEpic(EpicTask epicTask) {
        int id = generateId();
        epicTask.setId(id);
        taskData.put(id,epicTask);
    }

    public void addSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setId(id);
        taskData.put(id,subtask);
    }
}
