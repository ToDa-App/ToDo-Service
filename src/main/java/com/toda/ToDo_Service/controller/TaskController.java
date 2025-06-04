package com.toda.ToDo_Service.controller;

import com.toda.ToDo_Service.dto.TaskRequest;
import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.exception.ApiGenericResponse;
import com.toda.ToDo_Service.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    @PostMapping
    public ResponseEntity<ApiGenericResponse<Task>> createTask(
            @Validated(TaskRequest.OnCreate.class) @RequestBody TaskRequest request,
            Authentication authentication){
        try {
            Task task = taskService.createTask(request, authentication);
            return ResponseEntity.ok(ApiGenericResponse.success("Task created successfully", task));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiGenericResponse.error("Failed to create task: " + ex.getMessage()));
        }
    }
}
