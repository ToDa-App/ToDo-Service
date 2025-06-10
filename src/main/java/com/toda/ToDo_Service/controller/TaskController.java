package com.toda.ToDo_Service.controller;

import com.toda.ToDo_Service.dto.*;
import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import com.toda.ToDo_Service.exception.ApiGenericResponse;
import com.toda.ToDo_Service.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Task Management", description = "Operations related to creating, updating, deleting, and retrieving tasks")
public class TaskController {
    private final TaskService taskService;
    @Operation(summary = "Create a new task", description = "Creates a new task for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ApiGenericResponse<Task>> createTask(
            @Validated(TaskRequest.OnCreate.class) @RequestBody TaskRequest request,
            Authentication authentication){
        String userEmail = authentication.getName();
            Task task = taskService.createTask(request, userEmail);
            return ResponseEntity.ok(ApiGenericResponse.success("Task created successfully", task));
    }
    @Operation(summary = "Get active tasks", description = "Returns a paginated list of active (non-deleted) tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/active")
    public ResponseEntity<ApiGenericResponse<PagedResponse<TaskSummaryResponse>>> getActiveTasks(
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
    @Operation(summary = "Get deleted tasks", description = "Returns a paginated list of soft-deleted tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/deleted")
    public ResponseEntity<ApiGenericResponse<PagedResponse<TaskSummaryResponse>>> getDeletedTasks(
            @RequestParam Optional<Integer> page,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Page<TaskSummaryResponse> deletedTasks = taskService.getDeletedTasks(userEmail, page);
        PagedResponse<TaskSummaryResponse> pagedResponse = new PagedResponse<>(deletedTasks);
        String message = deletedTasks.isEmpty() ? "No Deleted tasks" : " Deleted Tasks retrieved successfully";
        return ResponseEntity.ok(ApiGenericResponse.success(message, pagedResponse));
    }
    @Operation(summary = "Get task by ID", description = "Retrieves the details of a specific task if not deleted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Task not found or deleted")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiGenericResponse<TaskDetailsResponse>> getTaskById(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        TaskDetailsResponse taskDetails = taskService.getTaskDetailsById(id, userEmail);
        return ResponseEntity.ok(ApiGenericResponse.success("Task retrieved successfully", taskDetails));
    }
    @Operation(summary = "Update a task", description = "Updates a task belonging to the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiGenericResponse<Task>> updateTask(
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Task updatedTask = taskService.updateTask(id, userEmail, request);
        return ResponseEntity.ok(ApiGenericResponse.success("Task updated successfully", updatedTask));
    }
    @Operation(summary = "Soft delete a task", description = "Marks a task as deleted instead of permanently deleting it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
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
    @Operation(summary = "Restore a deleted task", description = "Restores a soft-deleted task for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task restored successfully"),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Task not found or not deleted")
    })
    @PutMapping("/restore/{id}")
    public ResponseEntity<ApiGenericResponse<Void>> restoreTask(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        taskService.restoreTask(id, email);
        return ResponseEntity.ok(ApiGenericResponse.success("Task restored successfully", null));
    }
}
