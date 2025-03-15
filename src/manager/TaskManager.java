package manager;

import manager.exceptions.TaskNotFoundException;
import manager.exceptions.TaskTimeConflictException;
import model.Task;
import model.EpicTask;
import model.Subtask;

import java.util.List;

public interface TaskManager {
    void addTask(Task task) throws TaskTimeConflictException;

    void addEpic(EpicTask epicTask);

    void addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(EpicTask epicTask);

    void updateSubtask(Subtask subtask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id) throws TaskNotFoundException;

    List<Task> getAllTasks();

    List<Subtask> getSubtasksOfEpic(int epicId);

    Task getAnyTask(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

}
