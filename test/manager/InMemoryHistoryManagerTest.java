package manager;

import model.*;
import model.enums.StatusEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class InMemoryHistoryManagerTest {

    @Test
    void shouldAddTaskToHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task 1", "Description 1", StatusEnum.NEW);
        task.setId(1);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "Задача должна быть добавлена в историю.");
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task1 = new Task("Task 1", "Description 1", StatusEnum.NEW);
        Task task2 = new Task("Task 2", "Description 2", StatusEnum.NEW);
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления.");
        assertEquals(task2, history.get(0), "Оставшаяся задача должна быть корректной.");
    }

    @Test
    void shouldNotLimitHistorySize() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        // Добавляем 15 задач
        for (int i = 0; i < 15; i++) {
            Task task = new Task("Task " + i, "Description " + i, StatusEnum.NEW);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(15, history.size(), "История должна содержать все 15 задач без ограничения.");
    }
}