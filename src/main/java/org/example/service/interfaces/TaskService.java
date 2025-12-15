package org.example.service.interfaces;

import org.example.model.Task;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с задачами
 */
public interface TaskService {

    /**
     * Возвращает все задачи
     */
    List<Task> getAllTasks();

    /**
     * Ищет задачу по идентификатору
     */
    Optional<Task> getTaskById(Long id);

    /**
     * Создаёт новую задачу
     */
    Task createdTask(Task task);


    /**
     * Обновляет существующую задачу
     */
    Task updateTask(Long id, Task taskDetails);

    /**
     * Удаляет задачу по идентификатору
     */
    void deleteTask(Long id);
}

