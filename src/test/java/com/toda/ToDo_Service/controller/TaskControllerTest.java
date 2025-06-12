package com.toda.ToDo_Service.controller;

import com.toda.ToDo_Service.dto.*;
import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import com.toda.ToDo_Service.exception.ApiGenericResponse;
import com.toda.ToDo_Service.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {
    @InjectMocks
    private TaskController taskController;
    @Mock
    private TaskService taskService;
    @Mock
    private Authentication authentication;

    @Test
    void CreateTask_Success() {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);
        TaskRequest request = TaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .startDate(LocalDate.of(2025, 6, 12))
                .dueDate(LocalDate.of(2025, 6, 14))
                .priority("MEDIUM")
                .status("PENDING")
                .build();
        Task expectedTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .userEmail(email)
                .build();
        when(taskService.createTask(eq(request), eq(email))).thenReturn(expectedTask);
        ResponseEntity<ApiGenericResponse<Task>> response =
                taskController.createTask(request, authentication);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Task created successfully", response.getBody().getMessage());
        assertEquals(expectedTask, response.getBody().getData());
    }
    @Test
    void GetActiveTasks_Success() {
        // Arrange
        String email = "user@test.com";
        TaskSummaryResponse summary = TaskSummaryResponse.builder()
                .title("Test Task")
                .priority(TaskDetails.Priority.valueOf("MEDIUM"))
                .status(TaskDetails.Status.valueOf("PENDING"))
                .build();
        Page<TaskSummaryResponse> mockPage = new PageImpl<>(List.of(summary));
        when(authentication.getName()).thenReturn(email);
        when(taskService.getTasks(eq(email), any(), any(), any())).thenReturn(mockPage);
        ResponseEntity<ApiGenericResponse<PagedResponse<TaskSummaryResponse>>> response =
                taskController.getActiveTasks(Optional.of(0), Optional.empty(), Optional.empty(), authentication);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Tasks retrieved successfully", response.getBody().getMessage());
        assertFalse(response.getBody().getData().getContent().isEmpty());
        assertEquals(summary, response.getBody().getData().getContent().get(0));
    }
    @Test
    void GetActiveTasks_EmptyPage() {
        String email = "user@test.com";
        Page<TaskSummaryResponse> emptyPage = Page.empty();
        when(authentication.getName()).thenReturn(email);
        when(taskService.getTasks(eq(email), any(), any(), any())).thenReturn(emptyPage);
        ResponseEntity<ApiGenericResponse<PagedResponse<TaskSummaryResponse>>> response =
                taskController.getActiveTasks(Optional.of(0), Optional.empty(), Optional.empty(), authentication);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("No tasks found", response.getBody().getMessage());
        assertTrue(response.getBody().getData().getContent().isEmpty());
    }
    @Test
    void GetDeletedTasks_WithResults() {
        String email = "user@example.com";
        TaskSummaryResponse taskSummary = TaskSummaryResponse.builder()
                .title("Deleted Task")
                .priority(TaskDetails.Priority.valueOf("HIGH"))
                .status(TaskDetails.Status.valueOf("PENDING"))
                .build();
        Page<TaskSummaryResponse> mockPage = new PageImpl<>(List.of(taskSummary));
        when(authentication.getName()).thenReturn(email);
        when(taskService.getDeletedTasks(eq(email), any())).thenReturn(mockPage);
        ResponseEntity<ApiGenericResponse<PagedResponse<TaskSummaryResponse>>> response =
                taskController.getDeletedTasks(Optional.of(0), authentication);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(" Deleted Tasks retrieved successfully", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().getContent().size());
        assertEquals(taskSummary, response.getBody().getData().getContent().get(0));
    }
    @Test
    void GetDeletedTasks_Empty() {
        String email = "user@example.com";
        Page<TaskSummaryResponse> emptyPage = Page.empty();
        when(authentication.getName()).thenReturn(email);
        when(taskService.getDeletedTasks(eq(email), any())).thenReturn(emptyPage);
        ResponseEntity<ApiGenericResponse<PagedResponse<TaskSummaryResponse>>> response =
                taskController.getDeletedTasks(Optional.of(0), authentication);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("No Deleted tasks", response.getBody().getMessage());
        assertTrue(response.getBody().getData().getContent().isEmpty());
    }
    @Test
    void GetTaskById_Success() {
        Long taskId = 1L;
        String email = "user@example.com";
        TaskDetailsResponse responseMock = TaskDetailsResponse.builder()
                .title("Task Title")
                .description("Description")
                .status(TaskDetails.Status.valueOf("PENDING"))
                .priority(TaskDetails.Priority.valueOf("HIGH"))
                .build();
        when(authentication.getName()).thenReturn(email);
        when(taskService.getTaskDetailsById(taskId, email)).thenReturn(responseMock);
        ResponseEntity<ApiGenericResponse<TaskDetailsResponse>> response =
                taskController.getTaskById(taskId, authentication);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Task retrieved successfully", response.getBody().getMessage());
        assertEquals(responseMock, response.getBody().getData());
    }
    @Test
    void GetTaskById_NotFound() {
        Long taskId = 999L;
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);
        when(taskService.getTaskDetailsById(taskId, email))
                .thenThrow(new EntityNotFoundException("Task not found"));
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () ->
                taskController.getTaskById(taskId, authentication));
        assertEquals("Task not found", thrown.getMessage());
    }
    @Test
    void updateTask_shouldReturnSuccessResponse() {
        Long taskId = 1L;
        String email = "user@example.com";
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .priority("HIGH")
                .status("COMPLETED")
                .build();
        Task updatedTask = Task.builder()
                .id(taskId)
                .title("Updated Title")
                .userEmail(email)
                .taskDetails(TaskDetails.builder()
                        .description("Updated Description")
                        .priority(TaskDetails.Priority.HIGH)
                        .status(TaskDetails.Status.COMPLETED)
                        .build())
                .build();
        when(authentication.getName()).thenReturn(email);
        when(taskService.updateTask(taskId, email, request)).thenReturn(updatedTask);
        ResponseEntity<ApiGenericResponse<Task>> response = taskController.updateTask(taskId, request, authentication);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task updated successfully", response.getBody().getMessage());
        assertEquals(updatedTask, response.getBody().getData());
    }
    @Test
    void deleteTask_shouldReturnSuccessResponse() {
        Long taskId = 1L;
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);
        doNothing().when(taskService).deleteTask(taskId, email);
        ResponseEntity<ApiGenericResponse<Void>> response = taskController.deleteTask(taskId, authentication);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task deleted successfully", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(taskService, times(1)).deleteTask(taskId, email);
    }
    @Test
    void restoreTask_shouldReturnSuccessResponse() {
        Long taskId = 1L;
        String userEmail = "user@example.com";
        when(authentication.getName()).thenReturn(userEmail);
        doNothing().when(taskService).restoreTask(taskId, userEmail);
        ResponseEntity<ApiGenericResponse<Void>> response = taskController.restoreTask(taskId, authentication);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task restored successfully", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(taskService, times(1)).restoreTask(taskId, userEmail);
    }
}
