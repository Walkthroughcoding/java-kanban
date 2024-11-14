package manager;

public class Managers {

    // Метод для получения дефолтной реализации TaskManager
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(); // Возвращаем InMemoryTaskManager
    }
}
