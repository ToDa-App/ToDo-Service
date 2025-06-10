package com.toda.ToDo_Service.service;

import com.toda.ToDo_Service.dto.TaskDetailsResponse;
import com.toda.ToDo_Service.dto.TaskRequest;
import com.toda.ToDo_Service.dto.TaskSummaryResponse;
import com.toda.ToDo_Service.dto.UpdateTaskRequest;
import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface TaskService {
    Task createTask(TaskRequest request, String userEmail);
    Page<TaskSummaryResponse> getTasks(String userEmail, Optional<Integer> pageOpt, Optional<TaskDetails.Status> statusOpt, Optional<TaskDetails.Priority> priorityOpt);
    TaskDetailsResponse getTaskDetailsById(Long id, String userEmail);
    Task updateTask(Long id, String userEmail, UpdateTaskRequest request);
    void deleteTask(Long id, String userEmail);
    void restoreTask(Long id, String userEmail);
    Page<TaskSummaryResponse> getDeletedTasks(String userEmail, Optional<Integer> pageOpt);
}
