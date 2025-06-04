package com.toda.ToDo_Service.service;

import com.toda.ToDo_Service.dto.TaskRequest;
import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import com.toda.ToDo_Service.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;
    @Override
    public Task createTask(TaskRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        if (request.getStartDate() == null) {
            request.setStartDate(LocalDate.now());
        }
        if (request.getDueDate() == null) {
            request.setDueDate(request.getStartDate().plusDays(3));
        }
        TaskDetails details = TaskDetails.builder()
                .status(TaskDetails.Status.valueOf(request.getStatus()))
                .priority(TaskDetails.Priority.valueOf(request.getPriority()))
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .completionDate(request.getCompletionDate())
                .deleted(false)
                .build();
        Task task = Task.builder()
                .userEmail(userEmail) // تم تعديله بدل userId
                .title(request.getTitle())
                .taskDetails(details)
                .build();
        return taskRepository.save(task);
    }
}
