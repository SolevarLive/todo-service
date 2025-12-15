package org.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.Task;
import org.springframework.stereotype.Repository;

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
}
