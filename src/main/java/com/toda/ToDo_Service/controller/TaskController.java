package com.toda.ToDo_Service.controller;

import com.toda.ToDo_Service.dto.*;
import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import com.toda.ToDo_Service.exception.ApiGenericResponse;
import com.toda.ToDo_Service.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    @PostMapping
    public ResponseEntity<ApiGenericResponse<Task>> createTask(
            @Validated(TaskRequest.OnCreate.class) @RequestBody TaskRequest request,
            Authentication authentication){
        String userEmail = authentication.getName();
            Task task = taskService.createTask(request, userEmail);
            return ResponseEntity.ok(ApiGenericResponse.success("Task created successfully", task));
    }
    @GetMapping
    public ResponseEntity<ApiGenericResponse<PagedResponse<TaskSummaryResponse>>> getTasks(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<TaskDetails.Status> status,
            @RequestParam Optional<TaskDetails.Priority> priority,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Page<TaskSummaryResponse> onePage = taskService.getTasks(userEmail, page, status, priority);
        PagedResponse<TaskSummaryResponse> pagedResponse = new PagedResponse<>(onePage);
        String message = onePage.isEmpty() ? "No tasks found" : "Tasks retrieved successfully";
        return ResponseEntity.ok(ApiGenericResponse.success(message, pagedResponse));

    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiGenericResponse<TaskDetailsResponse>> getTaskById(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        TaskDetailsResponse taskDetails = taskService.getTaskDetailsById(id, userEmail);
        return ResponseEntity.ok(ApiGenericResponse.success("Task retrieved successfully", taskDetails));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiGenericResponse<Task>> updateTask(
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Task updatedTask = taskService.updateTask(id, userEmail, request);
        return ResponseEntity.ok(ApiGenericResponse.success("Task updated successfully", updatedTask));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiGenericResponse<Void>> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        taskService.deleteTask(id, userEmail);
        return ResponseEntity.ok(
                ApiGenericResponse.success("Task deleted successfully", null)
        );
    }
}
