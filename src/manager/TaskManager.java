package manager;

import model.Task;
import model.EpicTask;
import model.Subtask;
import model.enums.StatusEnum;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int currentId = 1;
    private HashMap<Integer, Task> taskData = new HashMap<>();
    private HashMap<Integer, EpicTask> epicData = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskData = new HashMap<>();

    public HashMap<Integer, Task> getTaskData() {
        return taskData;
    }

    public HashMap<Integer, EpicTask> getEpicData() {
        return epicData;
    }

    public HashMap<Integer, Subtask> getSubtaskData() {
        return subtaskData;
    }

    public int generateId() {
        return currentId++;
    }

    public void addTask(Task task) {
        int id = generateId();
        task.setId(id);
        taskData.put(id, task);
    }

    public void addEpic(EpicTask epicTask) {
        int id = generateId();
        epicTask.setId(id);
        epicData.put(id, epicTask);
    }

    public void addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        EpicTask epicTask = epicData.get(epicId);
        if (epicTask != null) {
            int subtaskId = generateId();
            subtask.setId(subtaskId);
            subtaskData.put(subtaskId, subtask);
            epicTask.getSubtaskIds().add(subtaskId); // Добавляем идентификатор подзадачи в эпик
        } else {
            System.out.println("Эпик с id " + epicId + " не найден. Подзадача не добавлена.");
        }
    }

    public void updateTask(Task task) {
        int id = task.getId();
        if (taskData.containsKey(id)) {
            taskData.put(id, task);
            System.out.println("Задача с id " + id + " обновлена.");
        } else {
            System.out.println("Задача с id " + id + " не найдена.");
        }
    }

    public void updateEpic(EpicTask epicTask) {
        int id = epicTask.getId();
        if (epicData.containsKey(id)) {
            epicData.put(id, epicTask);
            System.out.println("Эпик с id " + id + " обновлён.");
            if (epicTask != null) {
                updateEpicStatus(epicTask);
            }
        } else {
            System.out.println("Эпик с id " + id + " не найден.");
        }
    }

    private void updateEpicStatus(EpicTask epicTask) {
        ArrayList<Integer> subtaskIds = epicTask.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epicTask.setStatus(StatusEnum.NEW);
            return;
        }

        int newCount = 0;
        int doneCount = 0;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtaskData.get(subtaskId);
            if (subtask != null) {
                if (subtask.getStatus() == StatusEnum.NEW) {
                    newCount++;
                } else if (subtask.getStatus() == StatusEnum.DONE) {
                    doneCount++;
                }
            }
        }
        if (doneCount == subtaskIds.size()) {
            epicTask.setStatus(StatusEnum.DONE);
        } else if (newCount == subtaskIds.size()) {
            epicTask.setStatus(StatusEnum.NEW);
        } else {
            epicTask.setStatus(StatusEnum.IN_PROGRESS);
        }
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtaskData.containsKey(id)) {
            subtaskData.put(id, subtask);

            // Обновляем статус связанного эпика
            int epicId = subtask.getEpicId();
            EpicTask epicTask = epicData.get(epicId);
            if (epicTask != null) {
                updateEpicStatus(epicTask);
            }
            System.out.println("Подзадача с id " + id + " обновлена.");
        } else {
            System.out.println("Подзадача с id " + id + " не найдена.");
        }
    }

    public void deleteTask(int id) {
        Task task = taskData.remove(id);
        if (task != null) {
            System.out.println("Задача с id " + id + " удалена.");
        } else {
            System.out.println("Задача с id " + id + " не найдена.");
        }
    }

    public void deleteEpic(int id) {
        EpicTask epicTask = epicData.remove(id);
        if (epicTask != null) {
            // Удаляем все подзадачи, связанные с эпиком
            ArrayList<Integer> subtaskIds = epicTask.getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                subtaskData.remove(subtaskId);
            }
            System.out.println("Эпик с id " + id + " и все его подзадачи удалены.");
        } else {
            System.out.println("Эпик с id " + id + " не найден.");
        }
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtaskData.remove(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            EpicTask epicTask = epicData.get(epicId);
            if (epicTask != null) {
                epicTask.getSubtaskIds().remove((Integer) id); // Преобразуем id в Integer и удаляем его из списка subtaskIds
                updateEpicStatus(epicTask); // Обновляем статус эпика
            }
            System.out.println("Подзадача с id " + id + " удалена.");
        } else {
            System.out.println("Подзадача с id " + id + " не найдена.");
        }
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(taskData.values());
        allTasks.addAll(epicData.values());
        allTasks.addAll(subtaskData.values());
        return allTasks;
    }

    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        EpicTask epic = epicData.get(epicId);
        if (epic != null) {
            ArrayList<Subtask> subtasks = new ArrayList<>();
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                Subtask subtask = subtaskData.get(subtaskId);
                if (subtask != null) {
                    subtasks.add(subtask);
                }
            }
            return subtasks;
        } else {
            System.out.println("Эпик с id " + epicId + " не найден.");
            return new ArrayList<>();
        }
    }

    public Task getAnyTask(int id) {
        if (taskData.containsKey(id)) {
            return taskData.get(id);
        } else if (epicData.containsKey(id)) {
            return epicData.get(id);
        } else if (subtaskData.containsKey(id)) {
            return subtaskData.get(id);
        } else {
            System.out.println("Задача с id " + id + " не найдена.");
            return null;
        }
    }

}
