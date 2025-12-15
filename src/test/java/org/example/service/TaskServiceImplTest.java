package org.example.service;

import org.example.model.Task;
import org.example.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Проверяют работу основных методов сервиса задач с использованием моков репозитория
 */
class TaskServiceImplTest {

    private TaskRepository taskRepository;
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskServiceImpl(taskRepository);
    }

    /**
     * Должен возвращать список задач, полученный из репозитория
     */
    @Test
    void getAllTasks_returnsList() {
        when(taskRepository.findAll()).thenReturn(List.of(new Task()));

        List<Task> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        verify(taskRepository).findAll();
    }

    /**
     * Должен возвращать Optional с задачей, если она найдена по id
     */
    @Test
    void getTaskById_returnsOptional() {
        Task task = new Task();
        task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> result = taskService.getTaskById(1L);

        assertThat(result).isPresent();
        verify(taskRepository).findById(1L);
    }

    /**
     * Должен вызывать сохранение задачи в репозитории при создании
     */
    @Test
    void createdTask_callsSave() {
        Task task = new Task();
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.createdTask(task);

        assertThat(result).isSameAs(task);
        verify(taskRepository).save(task);
    }

    /**
     * Должен обновлять существующую задачу и сохранять изменения
     */
    @Test
    void updateTask_updatesAndSaves() {
        Task existing = new Task();
        existing.setId(1L);
        Task details = new Task();
        details.setTitle("New");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(existing)).thenReturn(existing);

        Task result = taskService.updateTask(1L, details);

        assertThat(result.getTitle()).isEqualTo("New");
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(existing);
    }

    /**
     * Должен бросать RuntimeException, если задача для обновления не найдена
     */
    @Test
    void updateTask_throwsWhenNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taskService.updateTask(1L, new Task()));
    }

    /**
     * Должен удалять задачу, если она существует
     */
    @Test
    void deleteTask_deletesWhenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    /**
     * Должен бросать RuntimeException при удалении несуществующей задачи
     */
    @Test
    void deleteTask_throwsWhenNotExists() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> taskService.deleteTask(1L));
    }

    /**
     * Должен запрашивать задачи за сегодня без фильтра по статусу выполнения
     */
    @Test
    void getTasksByPeriodAndStatus_callsRepositoryForToday() {
        taskService.getTasksByPeriodAndStatus("today", null);

        verify(taskRepository).findAllByDueDateBetween(any(), any());
    }

    /**
     * Должен запрашивать задачи за неделю с учётом статуса выполнения
     */
    @Test
    void getTasksByPeriodAndStatus_callsRepositoryForWeekWithCompleted() {
        taskService.getTasksByPeriodAndStatus("week", true);

        verify(taskRepository).findAllByDueDateBetweenAndCompleted(any(), any(), eq(true));
    }

    /**
     * Должен бросать IllegalArgumentException при неизвестном периоде
     */
    @Test
    void getTasksByPeriodAndStatus_throwsOnUnknownPeriod() {
        assertThrows(IllegalArgumentException.class,
                () -> taskService.getTasksByPeriodAndStatus("year", null));
    }
}
