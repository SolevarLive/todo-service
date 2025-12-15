package org.example.service;

import org.example.repository.TaskRepository;
import org.example.service.interfaces.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.model.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Реализация TaskService
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    /**
     * Создаёт сервис задач
     */
    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public Task createdTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Задача не найдена: " + id));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Задача не найдена: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> getTasksByPeriodAndStatus(String period, Boolean completed) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from;
        LocalDateTime to;

        switch (period) {
            case "today" -> {
                from = now.toLocalDate().atStartOfDay();
                to = from.plusDays(1);
            }
            case "week" -> {
                from = now.toLocalDate().atStartOfDay();
                to = from.plusWeeks(1);
            }
            case "month" -> {
                from = now.toLocalDate().atStartOfDay();
                to = from.plusMonths(1);
            }
            default -> throw new IllegalArgumentException("Unknown period: " + period);
        }

        if (completed == null) {
            return taskRepository.findAllByDueDateBetween(from, to);
        } else {
            return taskRepository.findAllByDueDateBetweenAndCompleted(from, to, completed);
        }
    }
}
