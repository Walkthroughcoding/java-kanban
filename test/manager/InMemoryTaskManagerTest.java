package manager;

import model.Task;
import model.EpicTask;
import model.Subtask;
import model.enums.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(); // Создаём новый менеджер перед каждым тестом
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test Task", "Test Description", StatusEnum.NEW);
        taskManager.addTask(task);
        Task retrievedTask = taskManager.getAnyTask(task.getId());

        assertNotNull(retrievedTask, "Задача не должна быть null.");
        assertEquals(task, retrievedTask, "Добавленная и полученная задачи должны совпадать.");
    }

    @Test
    void addNewEpicWithSubtasks() {
        EpicTask epic = new EpicTask("Test Epic", "Test Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Subtask Description", StatusEnum.NEW, epic.getId());
        taskManager.addSubtask(subtask);

        EpicTask retrievedEpic = (EpicTask) taskManager.getAnyTask(epic.getId());
        List<Subtask> subtasks = taskManager.getSubtasksOfEpic(epic.getId());

        assertNotNull(retrievedEpic, "Эпик не должен быть null.");
        assertEquals(1, subtasks.size(), "Эпик должен содержать одну подзадачу.");
        assertEquals(subtask, subtasks.get(0), "Подзадача должна совпадать с добавленной.");
    }

    @Test
    void cannotAddSubtaskToNonExistingEpic() {
        Subtask subtask = new Subtask("Orphan Subtask", "Should not be added", StatusEnum.NEW, 999);

        taskManager.addSubtask(subtask);
        assertNull(taskManager.getAnyTask(subtask.getId()), "Подзадача не должна быть добавлена к несуществующему эпику.");
    }

    @Test
    void taskEqualityById() {
        Task task1 = new Task("Task 1", "Description 1", StatusEnum.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task("Task 1", "Description 1", StatusEnum.NEW);
        task2.setId(task1.getId());

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны.");
    }

    @Test
    void epicStatusUpdatesWithSubtasks() {
        EpicTask epic = new EpicTask("Test Epic", "Test Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", StatusEnum.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", StatusEnum.DONE, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        EpicTask retrievedEpic = (EpicTask) taskManager.getAnyTask(epic.getId());
        assertEquals(StatusEnum.IN_PROGRESS, retrievedEpic.getStatus(), "Эпик должен быть IN_PROGRESS, если его подзадачи имеют разные статусы.");
    }

    @Test
    void deleteTaskRemovesTask() {
        Task task = new Task("Test Task", "Description", StatusEnum.NEW);
        taskManager.addTask(task);

        taskManager.deleteTask(task.getId());
        assertNull(taskManager.getAnyTask(task.getId()), "Удалённая задача не должна существовать.");
    }

    @Test
    void deleteSubtaskUpdatesEpic() {
        EpicTask epic = new EpicTask("Test Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", StatusEnum.DONE, epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.deleteSubtask(subtask.getId());
        EpicTask retrievedEpic = (EpicTask) taskManager.getAnyTask(epic.getId());
        assertEquals(StatusEnum.NEW, retrievedEpic.getStatus(), "Эпик должен быть NEW после удаления всех подзадач.");
    }

    @Test
    void historySizeLimit() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Task " + i, "Description " + i, StatusEnum.NEW);
            taskManager.addTask(task);
            taskManager.getAnyTask(task.getId()); // Добавляем в историю
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(10, history.size(), "История не должна превышать 10 элементов.");
    }
}