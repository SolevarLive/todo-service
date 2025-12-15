package org.example.controller;

import org.example.model.Task;
import org.example.service.interfaces.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты REST-контроллера TaskController с использованием MockMvc и Mockito
 */
 @WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    /**
     * MockMvc для выполнения HTTP-запросов к контроллеру
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Мок сервиса задач для изоляции контроллера от бизнес-логики
     */
    @MockBean
    private TaskService taskService;

    /**
     * Тестовая задача для использования во всех тестах
     */
    private Task sampleTask;

    /**
     * Инициализация тестовой задачи перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Test Task");
    }

    /**
     * Тест проверяет возврат списка всех задач со статусом 200
     */
    @Test
    @DisplayName("GET /api/tasks")
    void getAllTasks_returnsOk() throws Exception {
        List<Task> tasks = List.of(sampleTask);
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        verify(taskService).getAllTasks();
    }

    /**
     * Тест проверяет возврат найденной задачи со статусом 200
     */
    @Test
    @DisplayName("GET /api/tasks/{id}")
    void getTaskById_found_returnsOk() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(sampleTask));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService).getTaskById(1L);
    }

    /**
     * Тест проверяет возврат 404 при отсутствии задачи
     */
    @Test
    @DisplayName("GET /api/tasks/{id}")
    void getTaskById_notFound_returnsNotFound() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isNotFound());

        verify(taskService).getTaskById(1L);
    }

    /**
     * Тест POST /api/tasks - проверяет создание новой задачи со статусом 200
     */
    @Test
    @DisplayName("POST /api/tasks")
    void createTask_returnsCreatedTask() throws Exception {
        when(taskService.createdTask(any(Task.class))).thenReturn(sampleTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Task\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService).createdTask(any(Task.class));
    }

    /**
     * Тест проверяет успешное обновление задачи со статусом 200
     */
    @Test
    @DisplayName("PUT /api/tasks/{id}")
    void updateTask_success_returnsUpdatedTask() throws Exception {
        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(sampleTask);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Task\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService).updateTask(eq(1L), any(Task.class));
    }

    /**
     * Тест PUT /api/tasks/{id} - проверяет возврат 404 при ошибке обновления
     */
    @Test
    @DisplayName("PUT /api/tasks/{id}")
    void updateTask_notFound_returnsNotFound() throws Exception {
        when(taskService.updateTask(eq(1L), any(Task.class)))
                .thenThrow(new RuntimeException("Task not found"));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    /**
     * Тест проверяет успешное удаление со статусом 204
     */
    @Test
    @DisplayName("DELETE /api/tasks/{id}")
    void deleteTask_success_returnsNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    /**
     * Тест проверяет возврат 404 при ошибке удаления
     */
    @Test
    @DisplayName("DELETE /api/tasks/{id}")
    void deleteTask_notFound_returnsNotFound() throws Exception {
        doThrow(new RuntimeException("Task not found")).when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Тест проверяет фильтрацию по периоду и статусу со статусом 200
     */
    @Test
    @DisplayName("GET /api/tasks/filter")
    void getTasksByPeriodAndStatus_validParams_returnsOk() throws Exception {
        List<Task> tasks = List.of(sampleTask);
        when(taskService.getTasksByPeriodAndStatus("week", true)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/filter")
                        .param("period", "week")
                        .param("completed", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(taskService).getTasksByPeriodAndStatus("week", true);
    }

    /**
     * Тест проверяет возврат 400 при невалидном периоде
     */
    @Test
    @DisplayName("GET /api/tasks/filter")
    void getTasksByPeriodAndStatus_invalidPeriod_returnsBadRequest() throws Exception {
        when(taskService.getTasksByPeriodAndStatus("invalid", null))
                .thenThrow(new IllegalArgumentException("Invalid period"));

        mockMvc.perform(get("/api/tasks/filter")
                        .param("period", "invalid"))
                .andExpect(status().isBadRequest());
    }
}
