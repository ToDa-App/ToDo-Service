package com.toda.ToDo_Service.service;

import com.toda.ToDo_Service.dto.TaskRequest;
import com.toda.ToDo_Service.dto.TaskSummaryResponse;
import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import com.toda.ToDo_Service.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

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
    public Page<TaskSummaryResponse> getTasks(
            String userEmail,
            Optional<Integer> pageOpt,
            Optional<TaskDetails.Status> statusOpt,
            Optional<TaskDetails.Priority> priorityOpt) {
        int page = pageOpt.orElse(0);
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks;
        if (statusOpt.isPresent() && priorityOpt.isPresent()) {
            tasks = taskRepository.findByUserEmailAndTaskDetailsStatusAndTaskDetailsPriority(
                    userEmail, statusOpt.get(), priorityOpt.get(), pageable);
        } else if (statusOpt.isPresent()) {
            tasks = taskRepository.findByUserEmailAndTaskDetailsStatus(userEmail, statusOpt.get(), pageable);
        } else if (priorityOpt.isPresent()) {
            tasks = taskRepository.findByUserEmailAndTaskDetailsPriority(userEmail, priorityOpt.get(), pageable);
        } else {
            tasks = taskRepository.findByUserEmail(userEmail, pageable);
        }
        return tasks.map(task -> new TaskSummaryResponse(
                task.getTitle(),
                task.getTaskDetails().getPriority(),
                task.getTaskDetails().getStatus()
        ));
    }
}
