package com.toda.ToDo_Service.service;

import com.toda.ToDo_Service.dto.TaskDetailsResponse;
import com.toda.ToDo_Service.dto.TaskRequest;
import com.toda.ToDo_Service.dto.TaskSummaryResponse;
import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface TaskService {
    Task createTask(TaskRequest request, Authentication authentication);
    Page<TaskSummaryResponse> getTasks(String userEmail, Optional<Integer> pageOpt, Optional<TaskDetails.Status> statusOpt, Optional<TaskDetails.Priority> priorityOpt);
    TaskDetailsResponse getTaskDetailsById(Long id, String userEmail);
}
