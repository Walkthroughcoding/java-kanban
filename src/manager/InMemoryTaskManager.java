package manager;

import model.EpicTask;
import model.Subtask;
import model.Task;
import model.enums.StatusEnum;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private int currentId = 1;
    private HashMap<Integer, Task> taskData = new HashMap<>();
    private HashMap<Integer, EpicTask> epicData = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskData = new HashMap<>();
    private final ArrayList<Task> history = new ArrayList<>();

    public HashMap<Integer, Task> getTaskData() {
        return taskData;
    }

    public HashMap<Integer, EpicTask> getEpicData() {
        return epicData;
    }

    public HashMap<Integer, Subtask> getSubtaskData() {
        return subtaskData;
    }

    private int generateId() { // Исправлено на private
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
            epicTask.getSubtasks().add(subtask); // Добавляем подзадачу в эпик
            updateEpicStatus(epicTask);
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
            updateEpicStatus(epicTask); // Решил просто убрать ненужную проверку на null обьекта
        } else {
            System.out.println("Эпик с id " + id + " не найден.");
        }
    }

    // Сломал голову на этом методе
    private void updateEpicStatus(EpicTask epicTask) {
        ArrayList<Subtask> subtasks = epicTask.getSubtasks(); // Получаем список подзадач

        if (subtasks.isEmpty()) {
            epicTask.setStatus(StatusEnum.NEW);
            return;
        }

        ArrayList<StatusEnum> uniqueStatuses = new ArrayList<>();

        for (Subtask subtask : subtasks) {
            if (subtask != null) {
                StatusEnum status = subtask.getStatus();
                if (!uniqueStatuses.contains(status)) {
                    uniqueStatuses.add(status);
                }
            }
        }

        if (uniqueStatuses.size() == 1) {
            // Все подзадачи имеют один и тот же статус
            StatusEnum onlyStatus = uniqueStatuses.get(0);
            epicTask.setStatus(onlyStatus);
        } else {
            // Подзадачи имеют разные статусы
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
            ArrayList<Subtask> subtasks = epicTask.getSubtasks();
            for (Subtask subtask : subtasks) {
                subtaskData.remove(subtask.getId());
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
                epicTask.getSubtasks().remove(subtask); // Преобразуем id в Integer и удаляем его из списка subtaskIds
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
            return epic.getSubtasks();
        } else {
            System.out.println("Эпик с id " + epicId + " не найден.");
            return new ArrayList<>();
        }
    }

    @Override
    public Task getAnyTask(int id) {
        Task task = taskData.get(id);
        if (task == null) {
            task = epicData.get(id);
        }
        if (task == null) {
            task = subtaskData.get(id);
        }

        if (task != null) {
            addToHistory(task); // Добавляем в историю
        }
        return task;
    }

    private void addToHistory(Task task) {
        if (history.size() == 10) { // Лимит истории - 10 задач
            history.remove(0); // Удаляем самую старую задачу
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history); // Возвращаем копию списка
    }
}
