package org.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.Task;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с сущностью
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Возвращает все задачи с указанным статусом выполнения
     */
    List<Task> findAllByCompleted(boolean completed);

    /**
     * Возвращает все задачи, у которых дедлайн
     * находится в указанном диапазоне
     */
    List<Task> findAllByDueDateBetween(LocalDateTime from, LocalDateTime to);

    /**
     * Возвращает все задачи, у которых дедлайн
     * находится в указанном диапазоне и заданный статус выполнения
     */
    List<Task> findAllByDueDateBetweenAndCompleted(LocalDateTime from, LocalDateTime to, boolean completed);
}
