package manager;

import model.EpicTask;
import model.Subtask;
import model.Task;
import model.enums.StatusEnum;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int currentId = 1;

    protected Map<Integer, Task> taskData = new HashMap<>();
    protected Map<Integer, EpicTask> epicData = new HashMap<>();
    protected Map<Integer, Subtask> subtaskData = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));

    public Map<Integer, Task> getTaskData() {
        return taskData;
    }

    public Map<Integer, EpicTask> getEpicData() {
        return epicData;
    }

    public Map<Integer, Subtask> getSubtaskData() {
        return subtaskData;
    }

    private int generateId() { // Исправлено на private
        return currentId++;
    }

    public void addTask(Task task) {
        if (!isOverlapping(task)) {
            int id = generateId();
            task.setId(id);
            taskData.put(id, task);
            prioritizedTasks.add(task);
            System.out.println("Задача с id " + id + " добавлена.");
        } else {
            throw new IllegalArgumentException("Ошибка: Задача пересекается по времени с другой задачей.");
        }
    }

    protected void setCurrentId(int currentId) {
        this.currentId = currentId;
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
            if (!isOverlapping(subtask)) {
                int subtaskId = generateId();
                subtask.setId(subtaskId);
                subtaskData.put(subtaskId, subtask);
                updateEpicStatus(epicTask);  // Вызов обновления статуса эпика
                prioritizedTasks.add(subtask);
                System.out.println("Подзадача с id " + subtaskId + " добавлена в эпик " + epicId);
            } else {
                System.out.println("Ошибка: Подзадача пересекается по времени с другой задачей.");
            }
        } else {
            System.out.println("Ошибка: Эпик с id " + epicId + " не найден. Подзадача не добавлена.");
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
        if (epicTask == null) return;

        List<Subtask> subtasks = getSubtasksOfEpic(epicTask.getId());

        if (subtasks.isEmpty()) {
            epicTask.setStatus(StatusEnum.NEW);
            return;
        }

        boolean allDone = true;
        boolean hasNew = false;
        boolean hasInProgress = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == StatusEnum.NEW) {
                hasNew = true;
                allDone = false;
            } else if (subtask.getStatus() == StatusEnum.IN_PROGRESS) {
                hasInProgress = true;
                allDone = false;
            } else if (subtask.getStatus() != StatusEnum.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epicTask.setStatus(StatusEnum.DONE);
        } else if (hasInProgress || (hasNew && !allDone)) {
            epicTask.setStatus(StatusEnum.IN_PROGRESS);
        } else {
            epicTask.setStatus(StatusEnum.NEW);
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

    public void deleteEpic(int id) {
        EpicTask epicTask = epicData.remove(id);
        if (epicTask != null) {
            List<Subtask> subtasks = getSubtasksOfEpic(id);
            for (Subtask subtask : subtasks) {
                subtaskData.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
            System.out.println("Эпик с id " + id + " и все его подзадачи удалены.");
        } else {
            System.out.println("Эпик с id " + id + " не найден.");
        }
    }

    public void deleteTask(int id) {
        Task task = taskData.remove(id);
        if (task != null) {
            historyManager.remove(id);
            prioritizedTasks.remove(task);
            System.out.println("Задача с id " + id + " удалена.");
        } else {
            System.out.println("Задача с id " + id + " не найдена.");
        }
    }

    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(taskData.values());
        allTasks.addAll(epicData.values());
        allTasks.addAll(subtaskData.values());
        return allTasks;
    }

    public List<Subtask> getSubtasksOfEpic(int epicId) {
        return subtaskData.values().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .toList();
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
            historyManager.add(task); // Добавляем в историю
        }
        return task;
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtaskData.remove(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            EpicTask epicTask = epicData.get(epicId);
            if (epicTask != null) {
                epicTask.getSubtasks().remove(subtask); //  Удаляем подзадачу из эпика
                updateEpicStatus(epicTask);
            }
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
            System.out.println("Подзадача с id " + id + " удалена.");
        } else {
            System.out.println("Ошибка: Подзадача с id " + id + " не найдена.");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory(); // Возвращаем копию списка
    }

    private boolean isOverlapping(Task newTask) {
        return prioritizedTasks.stream().anyMatch(existingTask ->
                newTask.getStartTime() != null && existingTask.getStartTime() != null &&
                        newTask.getEndTime().isAfter(existingTask.getStartTime()) &&
                        newTask.getStartTime().isBefore(existingTask.getEndTime()));
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}