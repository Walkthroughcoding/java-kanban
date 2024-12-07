package manager;

public class Managers {

    // Метод для получения дефолтной реализации TaskManager
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(); // Возвращаем InMemoryTaskManager
    }

    // Метод для получения дефолтной реализации HistoryManager
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager(); // Возвращаем InMemoryHistoryManager
    }
}
