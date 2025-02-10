package manager;

import model.Task;
import model.enums.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws Exception {
        tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();
        manager = FileBackedTaskManager.create(tempFile);
    }

    @Test
    void save() {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW);
        manager.addTask(task);

        manager.save();

        assertTrue(tempFile.length() > 0, "Файл должен быть не пустым после сохранения.");
    }

    @Test
    void loadFromFile() {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW);
        manager.addTask(task);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size(), "Должна быть загружена одна задача.");
        assertEquals("Task 1", loadedManager.getAllTasks().get(0).getTitle(), "Название загруженной задачи должно совпадать.");
    }

    @Test
    void addTask() {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW);
        manager.addTask(task);

        assertEquals(1, manager.getAllTasks().size(), "Должна быть добавлена одна задача.");
        assertEquals("Task 1", manager.getAllTasks().get(0).getTitle(), "Название добавленной задачи должно совпадать.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW);
        manager.addTask(task);

        task.setTitle("Updated Task 1");
        manager.updateTask(task);

        assertEquals("Updated Task 1", manager.getAllTasks().get(0).getTitle(), "Название задачи должно обновиться.");
    }

    @Test
    void deleteTask() {
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW);
        manager.addTask(task);

        manager.deleteTask(task.getId());

        assertTrue(manager.getAllTasks().isEmpty(), "Все задачи должны быть удалены.");
    }
}