package manager;

import model.*;
import model.enums.StatusEnum;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : taskData.values()) {
                writer.write(toString(task) + "\n");
            }
            for (EpicTask epic : epicData.values()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : subtaskData.values()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении задач в файл: " + e.getMessage());
        }
    }

    private String toString(Task task) {
        String type;
        if (task instanceof EpicTask) {
            type = "EPIC";
        } else if (task instanceof Subtask) {
            type = "SUBTASK";
        } else {
            type = "TASK";
        }

        return task.getId() + "," + type + "," +
                task.getTitle() + "," + task.getStatus() + "," +
                task.getDescription() +
                (task instanceof Subtask ? "," + ((Subtask) task).getEpicId() : "");
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            int maxId = 0;
            List<Subtask> tempSubtasks = new ArrayList<>();

            for (int i = 1; i < lines.size(); i++) { // Пропускаем заголовок
                Task task = fromString(lines.get(i));

                if (task instanceof EpicTask) {
                    manager.epicData.put(task.getId(), (EpicTask) task);
                } else if (task instanceof Subtask) {
                    tempSubtasks.add((Subtask) task);
                } else {
                    manager.taskData.put(task.getId(), task);
                }

                maxId = Math.max(maxId, task.getId());
            }

            // Привязываем подзадачи к эпику
            for (Subtask subtask : tempSubtasks) {
                EpicTask epic = manager.epicData.get(subtask.getEpicId());
                if (epic != null) {
                    epic.getSubtasks().add(subtask);
                    manager.subtaskData.put(subtask.getId(), subtask);
                }
            }

            manager.setCurrentId(maxId + 1);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке задач из файла: " + e.getMessage());
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        StatusEnum status = StatusEnum.valueOf(fields[3]);
        String description = fields[4];

        Task task;
        switch (type) {
            case "TASK":
                task = new Task(name, description, status);
                break;
            case "EPIC":
                task = new EpicTask(name, description);
                break;
            case "SUBTASK":
                int epicId = Integer.parseInt(fields[5]);
                task = new Subtask(name, description, status, epicId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        task.setId(id);
        return task;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(EpicTask epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }
}

