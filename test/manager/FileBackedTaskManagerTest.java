package manager;

import model.Task;
import model.enums.StatusEnum;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void save() throws Exception {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW);
        manager.addTask(task);

        manager.save();

        assertTrue(tempFile.length() > 0);
    }

    @Test
    void loadFromFile() throws Exception {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW);
        manager.addTask(task);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals("Task 1", loadedManager.getAllTasks().get(0).getTitle());
    }

    @Test
    void addTask() throws Exception {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW);
        manager.addTask(task);

        assertEquals(1, manager.getAllTasks().size());
        assertEquals("Task 1", manager.getAllTasks().get(0).getTitle());
    }

    @Test
    void updateTask() throws Exception {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW);
        manager.addTask(task);

        task.setTitle("Updated Task 1");
        manager.updateTask(task);

        assertEquals("Updated Task 1", manager.getAllTasks().get(0).getTitle());
    }

    @Test
    void deleteTask() throws Exception {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW);
        manager.addTask(task);

        manager.deleteTask(task.getId());

        assertTrue(manager.getAllTasks().isEmpty());
    }
}