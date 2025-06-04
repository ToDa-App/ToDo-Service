package com.toda.ToDo_Service.service;

import com.toda.ToDo_Service.dto.TaskRequest;
import com.toda.ToDo_Service.entity.Task;
import org.springframework.security.core.Authentication;

public interface TaskService {
    public Task createTask(TaskRequest request, Authentication authentication);
}
