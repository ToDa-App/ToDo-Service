package com.toda.ToDo_Service.service;

import com.toda.ToDo_Service.dto.TaskDetailsResponse;
import com.toda.ToDo_Service.dto.TaskRequest;
import com.toda.ToDo_Service.dto.TaskSummaryResponse;
import com.toda.ToDo_Service.dto.UpdateTaskRequest;
import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import com.toda.ToDo_Service.repository.TaskRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskServiceImpl taskService;
    @Test
    void createTask_withFullDates_shouldCreateTaskSuccessfully() {
        String userEmail = "user@example.com";
        LocalDate start = LocalDate.of(2025, 6, 10);
        LocalDate due = LocalDate.of(2025, 6, 13);
        TaskRequest request = TaskRequest.builder()
                .title("Test Task")
                .status("PENDING")
                .priority("HIGH")
                .description("Test description")
                .startDate(start)
                .dueDate(due)
                .completionDate(null)
                .build();
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        Task saved = taskService.createTask(request, userEmail);
        verify(taskRepository).save(taskCaptor.capture());
        Task captured = taskCaptor.getValue();
        assertEquals(userEmail, captured.getUserEmail());
        assertEquals("Test Task", captured.getTitle());
        assertEquals("Test description", captured.getTaskDetails().getDescription());
        assertEquals(TaskDetails.Status.PENDING, captured.getTaskDetails().getStatus());
        assertEquals(TaskDetails.Priority.HIGH, captured.getTaskDetails().getPriority());
        assertEquals(start, captured.getTaskDetails().getStartDate());
        assertEquals(due, captured.getTaskDetails().getDueDate());
        assertFalse(captured.getTaskDetails().isDeleted());
    }
    @Test
    void createTask_whenStartDateAndDueDateNull_shouldSetDefaults() {
        String userEmail = "user@example.com";
        LocalDate today = LocalDate.now();
        TaskRequest request = TaskRequest.builder()
                .title("Task")
                .status("PENDING")
                .priority("LOW")
                .description("desc")
                .startDate(null)
                .dueDate(null)
                .build();
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        Task result = taskService.createTask(request, userEmail);
        assertEquals(today, result.getTaskDetails().getStartDate());
        assertEquals(today.plusDays(3), result.getTaskDetails().getDueDate());
    }
    @Test
    void createTask_whenDueDateNull_shouldSetDueDateBasedOnStartDate() {
        String userEmail = "user@example.com";
        LocalDate start = LocalDate.of(2025, 6, 1);
        TaskRequest request = TaskRequest.builder()
                .title("Task")
                .status("PENDING")
                .priority("MEDIUM")
                .description("desc")
                .startDate(start)
                .dueDate(null)
                .build();
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        Task result = taskService.createTask(request, userEmail);
        assertEquals(start, result.getTaskDetails().getStartDate());
        assertEquals(start.plusDays(3), result.getTaskDetails().getDueDate());
    }
    @Test
    void createTask_whenStartDateNull_shouldSetStartToTodayAndHandleDueDate() {
        String userEmail = "user@example.com";
        LocalDate today = LocalDate.now();
        LocalDate due = today.plusDays(5);
        TaskRequest request = TaskRequest.builder()
                .title("Task")
                .status("COMPLETED")
                .priority("LOW")
                .description("desc")
                .startDate(null)
                .dueDate(due)
                .build();
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        Task result = taskService.createTask(request, userEmail);
        assertEquals(today, result.getTaskDetails().getStartDate());
        assertEquals(due, result.getTaskDetails().getDueDate());
    }
    @Test
    void getTaskDetailsById_success() {
        String userEmail = "user@example.com";
        Long taskId = 1L;
        TaskDetails details = TaskDetails.builder()
                .description("Test description")
                .status(TaskDetails.Status.PENDING)
                .priority(TaskDetails.Priority.MEDIUM)
                .startDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(3))
                .completionDate(null)
                .deleted(false)
                .build();
        Task task = Task.builder()
                .id(taskId)
                .userEmail(userEmail)
                .title("Test Task")
                .taskDetails(details)
                .build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        TaskDetailsResponse response = taskService.getTaskDetailsById(taskId, userEmail);
        assertEquals("Test Task", response.getTitle());
        assertEquals(TaskDetails.Status.PENDING, response.getStatus());
        assertEquals(TaskDetails.Priority.MEDIUM, response.getPriority());
        assertEquals("Test description", response.getDescription());
        verify(taskRepository).findById(taskId);
    }
    @Test
    void getTaskDetailsById_deletedTask_throwsException() {
        String userEmail = "user@example.com";
        Long taskId = 1L;
        TaskDetails details = TaskDetails.builder()
                .deleted(true)
                .build();
        Task task = Task.builder()
                .id(taskId)
                .userEmail(userEmail)
                .title("Deleted Task")
                .taskDetails(details)
                .build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.getTaskDetailsById(taskId, userEmail));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("This task is deleted"));
        verify(taskRepository).findById(taskId);
    }
    @Test
    void getTaskDetailsById_taskNotFound_throwsException() {
        String userEmail = "user@example.com";
        Long taskId = 2L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.getTaskDetailsById(taskId, userEmail));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Task not found"));
        verify(taskRepository).findById(taskId);
    }
    @Test
    void getTaskDetailsById_notUserTask_throwsException() {
        String currentUser = "user@example.com";
        String otherUser = "another@example.com";
        Long taskId = 3L;
        TaskDetails details = TaskDetails.builder()
                .deleted(false)
                .build();
        Task task = Task.builder()
                .id(taskId)
                .userEmail(otherUser)
                .title("Other User Task")
                .taskDetails(details)
                .build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.getTaskDetailsById(taskId, currentUser));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Not authorized"));
        verify(taskRepository).findById(taskId);
    }
    @Test
    void deleteTask_successfullyMarksTaskAsDeleted() {
        Long taskId = 1L;
        String userEmail = "user@example.com";
        TaskDetails details = TaskDetails.builder()
                .deleted(false)
                .build();
        Task task = Task.builder()
                .id(taskId)
                .userEmail(userEmail)
                .taskDetails(details)
                .build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        taskService.deleteTask(taskId, userEmail);
        assertTrue(task.getTaskDetails().isDeleted());
        assertNotNull(task.getTaskDetails().getDeletedAt());
        verify(taskRepository).save(task);
    }
    @Test
    void deleteTask_taskNotFound_throwsNotFound() {
        Long taskId = 1L;
        String userEmail = "user@example.com";
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> taskService.deleteTask(taskId, userEmail));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
    @Test
    void deleteTask_notOwner_throwsForbidden() {
        Long taskId = 1L;
        String userEmail = "user@example.com";
        String anotherUser = "hacker@example.com";
        Task task = Task.builder()
                .id(taskId)
                .userEmail(anotherUser)
                .taskDetails(TaskDetails.builder().deleted(false).build())
                .build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> taskService.deleteTask(taskId, userEmail));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
    @Test
    void deleteTask_alreadyDeleted_throwsBadRequest() {
        Long taskId = 1L;
        String userEmail = "user@example.com";
        TaskDetails details = TaskDetails.builder()
                .deleted(true)
                .build();
        Task task = Task.builder()
                .id(taskId)
                .userEmail(userEmail)
                .taskDetails(details)
                .build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> taskService.deleteTask(taskId, userEmail));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
    @Test
    void restoreTask_successfullyRestoresDeletedTask() {
        Long taskId = 1L;
        String userEmail = "user@example.com";
        TaskDetails details = TaskDetails.builder()
                .deleted(true)
                .deletedAt(LocalDateTime.now())
                .build();
        Task task = Task.builder()
                .id(taskId)
                .userEmail(userEmail)
                .taskDetails(details)
                .build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        taskService.restoreTask(taskId, userEmail);
        assertFalse(task.getTaskDetails().isDeleted());
        assertNull(task.getTaskDetails().getDeletedAt());
        verify(taskRepository).save(task);
    }
    @Test
    void restoreTask_taskNotFound_throwsNotFound() {
        Long taskId = 1L;
        String userEmail = "user@example.com";
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> taskService.restoreTask(taskId, userEmail));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
    @Test
    void restoreTask_notOwner_throwsForbidden() {
        Long taskId = 1L;
        String userEmail = "user@example.com";
        String anotherUser = "other@example.com";
        Task task = Task.builder()
                .id(taskId)
                .userEmail(anotherUser)
                .taskDetails(TaskDetails.builder().deleted(true).build())
                .build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> taskService.restoreTask(taskId, userEmail));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
    @Test
    void restoreTask_notDeleted_throwsBadRequest() {
        Long taskId = 1L;
        String userEmail = "user@example.com";
        TaskDetails details = TaskDetails.builder()
                .deleted(false)
                .build();
        Task task = Task.builder()
                .id(taskId)
                .userEmail(userEmail)
                .taskDetails(details)
                .build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> taskService.restoreTask(taskId, userEmail));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
    @Test
    void updateTask_whenTaskNotFound_shouldThrowNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("New Title")
                .description("Updated desc")
                .priority("HIGH")
                .status("COMPLETED")
                .startDate(LocalDate.of(2025, 6, 10))
                .dueDate(LocalDate.of(2025, 6, 12))
                .completionDate(LocalDate.of(2025, 6, 11))
                .build();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                taskService.updateTask(1L, "user@example.com", request));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
    @Test
    void updateTask_whenUnauthorizedUser_shouldThrowForbidden() {
        Task task = Task.builder().id(1L).userEmail("other@example.com").taskDetails(TaskDetails.builder().deleted(false).build()).build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        UpdateTaskRequest req = UpdateTaskRequest.builder()
                .title("New Title")
                .description("Updated desc")
                .priority("HIGH")
                .status("COMPLETED")
                .startDate(LocalDate.of(2025, 6, 10))
                .dueDate(LocalDate.of(2025, 6, 12))
                .completionDate(LocalDate.of(2025, 6, 11))
                .build();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                taskService.updateTask(1L, "user@example.com", req));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
    @Test
    void updateTask_whenTaskIsDeleted_shouldThrowBadRequest() {
        Task task = Task.builder()
                .id(1L)
                .userEmail("user@example.com")
                .taskDetails(TaskDetails.builder().deleted(true).build())
                .build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        UpdateTaskRequest req = UpdateTaskRequest.builder()
                .title("New Title")
                .description("Updated desc")
                .priority("HIGH")
                .status("COMPLETED")
                .startDate(LocalDate.of(2025, 6, 10))
                .dueDate(LocalDate.of(2025, 6, 12))
                .completionDate(LocalDate.of(2025, 6, 11))
                .build();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                taskService.updateTask(1L, "user@example.com", req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
    @Test
    void updateTask_withValidFields_shouldUpdateAndSave() {
        TaskDetails details = TaskDetails.builder()
                .description("old")
                .priority(TaskDetails.Priority.LOW)
                .status(TaskDetails.Status.PENDING)
                .startDate(LocalDate.of(2025, 6, 1))
                .dueDate(LocalDate.of(2025, 6, 4))
                .completionDate(null)
                .deleted(false)
                .build();
        Task task = Task.builder()
                .id(1L)
                .userEmail("user@example.com")
                .title("Old Title")
                .taskDetails(details)
                .build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        UpdateTaskRequest req = UpdateTaskRequest.builder()
                .title("New Title")
                .description("Updated desc")
                .priority("HIGH")
                .status("COMPLETED")
                .startDate(LocalDate.of(2025, 6, 10))
                .dueDate(LocalDate.of(2025, 6, 12))
                .completionDate(LocalDate.of(2025, 6, 11))
                .build();
        Task updated = taskService.updateTask(1L, "user@example.com", req);
        assertEquals("New Title", updated.getTitle());
        assertEquals("Updated desc", updated.getTaskDetails().getDescription());
        assertEquals(TaskDetails.Priority.HIGH, updated.getTaskDetails().getPriority());
        assertEquals(TaskDetails.Status.COMPLETED, updated.getTaskDetails().getStatus());
        assertEquals(LocalDate.of(2025, 6, 10), updated.getTaskDetails().getStartDate());
        assertEquals(LocalDate.of(2025, 6, 12), updated.getTaskDetails().getDueDate());
        assertEquals(LocalDate.of(2025, 6, 11), updated.getTaskDetails().getCompletionDate());
    }
    @Test
    void updateTask_withBlankTitle_shouldNotUpdateTitle() {
        TaskDetails details = TaskDetails.builder().deleted(false).build();
        Task task = Task.builder().id(1L).userEmail("user@example.com").title("Keep Title").taskDetails(details).build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        UpdateTaskRequest req = UpdateTaskRequest.builder().title("  ").build();
        Task updated = taskService.updateTask(1L, "user@example.com", req);
        assertEquals("Keep Title", updated.getTitle());
    }
    @Test
    void GetTasks_withoutFilters_returnsPage() {
        Task task = Task.builder()
                .title("Task1")
                .userEmail("user@example.com")
                .taskDetails(TaskDetails.builder()
                        .priority(TaskDetails.Priority.MEDIUM)
                        .status(TaskDetails.Status.PENDING)
                        .deleted(false)
                        .build())
                .build();
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        when(taskRepository.findByUserEmailAndTaskDetailsDeletedFalse(eq("user@example.com"), any(Pageable.class)))
                .thenReturn(taskPage);
        Page<TaskSummaryResponse> result = taskService.getTasks("user@example.com", Optional.of(0), Optional.empty(), Optional.empty());
        assertEquals(1, result.getContent().size());
        assertEquals("Task1", result.getContent().get(0).getTitle());
    }
    @Test
    void GetTasks_withStatusFilter() {
        Task task = Task.builder()
                .title("Task with Status")
                .userEmail("user@example.com")
                .taskDetails(TaskDetails.builder()
                        .priority(TaskDetails.Priority.LOW)
                        .status(TaskDetails.Status.COMPLETED)
                        .deleted(false)
                        .build())
                .build();
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        when(taskRepository.findByUserEmailAndTaskDetailsStatusAndTaskDetailsDeletedFalse(
                eq("user@example.com"), eq(TaskDetails.Status.COMPLETED), any(Pageable.class)))
                .thenReturn(taskPage);
        Page<TaskSummaryResponse> result = taskService.getTasks(
                "user@example.com", Optional.of(0),
                Optional.of(TaskDetails.Status.COMPLETED), Optional.empty());
        assertEquals(1, result.getContent().size());
        assertEquals(TaskDetails.Status.COMPLETED, result.getContent().get(0).getStatus());
    }
    @Test
    void GetTasks_withPriorityFilter() {
        Task task = Task.builder()
                .title("Priority Task")
                .userEmail("user@example.com")
                .taskDetails(TaskDetails.builder()
                        .priority(TaskDetails.Priority.HIGH)
                        .status(TaskDetails.Status.PENDING)
                        .deleted(false)
                        .build())
                .build();
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        when(taskRepository.findByUserEmailAndTaskDetailsPriorityAndTaskDetailsDeletedFalse(
                eq("user@example.com"), eq(TaskDetails.Priority.HIGH), any(Pageable.class)))
                .thenReturn(taskPage);
        Page<TaskSummaryResponse> result = taskService.getTasks(
                "user@example.com", Optional.of(0),
                Optional.empty(), Optional.of(TaskDetails.Priority.HIGH));
        assertEquals(1, result.getContent().size());
        assertEquals(TaskDetails.Priority.HIGH, result.getContent().get(0).getPriority());
    }
    @Test
    void GetTasks_withStatusAndPriorityFilters() {
        Task task = Task.builder()
                .title("Filtered Task")
                .userEmail("user@example.com")
                .taskDetails(TaskDetails.builder()
                        .priority(TaskDetails.Priority.HIGH)
                        .status(TaskDetails.Status.COMPLETED)
                        .deleted(false)
                        .build())
                .build();
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        when(taskRepository.findByUserEmailAndTaskDetailsStatusAndTaskDetailsPriorityAndTaskDetailsDeletedFalse(
                eq("user@example.com"), eq(TaskDetails.Status.COMPLETED), eq(TaskDetails.Priority.HIGH), any(Pageable.class)))
                .thenReturn(taskPage);
        Page<TaskSummaryResponse> result = taskService.getTasks(
                "user@example.com", Optional.of(0),
                Optional.of(TaskDetails.Status.COMPLETED),
                Optional.of(TaskDetails.Priority.HIGH));
        assertEquals(1, result.getContent().size());
        assertEquals("Filtered Task", result.getContent().get(0).getTitle());
    }
    @Test
    void GetDeletedTasks_returnsDeletedPage() {
        Task task = Task.builder()
                .title("Deleted Task")
                .userEmail("user@example.com")
                .taskDetails(TaskDetails.builder()
                        .priority(TaskDetails.Priority.MEDIUM)
                        .status(TaskDetails.Status.PENDING)
                        .deleted(true)
                        .build())
                .build();
        Page<Task> deletedPage = new PageImpl<>(List.of(task));
        when(taskRepository.findByUserEmailAndTaskDetails_DeletedTrue(eq("user@example.com"), any(Pageable.class)))
                .thenReturn(deletedPage);
        Page<TaskSummaryResponse> result = taskService.getDeletedTasks("user@example.com", Optional.of(0));
        assertEquals(1, result.getContent().size());
        assertEquals("Deleted Task", result.getContent().get(0).getTitle());
    }

}
