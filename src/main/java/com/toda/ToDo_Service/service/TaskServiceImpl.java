package com.toda.ToDo_Service.service;

import com.toda.ToDo_Service.dto.TaskDetailsResponse;
import com.toda.ToDo_Service.dto.TaskRequest;
import com.toda.ToDo_Service.dto.TaskSummaryResponse;
import com.toda.ToDo_Service.dto.UpdateTaskRequest;
import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import com.toda.ToDo_Service.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.springframework.http.HttpStatus.NOT_FOUND;
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;
    @Override
    public Task createTask(TaskRequest request, String userEmail) {
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
                .userEmail(userEmail)
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
            tasks = taskRepository.findByUserEmailAndTaskDetailsStatusAndTaskDetailsPriorityAndTaskDetailsDeletedFalse(
                    userEmail, statusOpt.get(), priorityOpt.get(), pageable);
        } else if (statusOpt.isPresent()) {
            tasks = taskRepository.findByUserEmailAndTaskDetailsStatusAndTaskDetailsDeletedFalse(userEmail, statusOpt.get(), pageable);
        } else if (priorityOpt.isPresent()) {
            tasks = taskRepository.findByUserEmailAndTaskDetailsPriorityAndTaskDetailsDeletedFalse(userEmail, priorityOpt.get(), pageable);
        } else {
            tasks = taskRepository.findByUserEmailAndTaskDetailsDeletedFalse(userEmail, pageable);
        }
        return tasks.map(task -> new TaskSummaryResponse(
                task.getId(),
                task.getTitle(),
                task.getTaskDetails().getPriority(),
                task.getTaskDetails().getStatus()
        ));
    }
    public Page<TaskSummaryResponse> getDeletedTasks(String userEmail, Optional<Integer> pageOpt) {
        int page = pageOpt.orElse(0);
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> deletedTasks = taskRepository.findByUserEmailAndTaskDetails_DeletedTrue(userEmail, pageable);
        return deletedTasks.map(task -> new TaskSummaryResponse(
                task.getId(),
                task.getTitle(),
                task.getTaskDetails().getPriority(),
                task.getTaskDetails().getStatus()
        ));
    }
    public TaskDetailsResponse getTaskDetailsById(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Task not found"));
        if (!task.getUserEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to access this task");
        }
        if (task.getTaskDetails().isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This task is deleted. Please restore it first.");
        }
        TaskDetails taskDetails = task.getTaskDetails();
        return TaskDetailsResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(taskDetails.getDescription())
                .status(taskDetails.getStatus())
                .priority(taskDetails.getPriority())
                .startDate(taskDetails.getStartDate())
                .dueDate(taskDetails.getDueDate())
                .completionDate(taskDetails.getCompletionDate())
                .build();
    }
    public Task updateTask(Long id, String userEmail, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (!task.getUserEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to update this task");
        }
        if (task.getTaskDetails().isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This task is deleted. Please restore it first.");
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }
        TaskDetails details = task.getTaskDetails();
        if (request.getDescription() != null)
            details.setDescription(request.getDescription());
        if (request.getPriority() != null)
            details.setPriority(TaskDetails.Priority.valueOf(request.getPriority()));
        if (request.getStatus() != null)
            details.setStatus(TaskDetails.Status.valueOf(request.getStatus()));
        if (request.getStartDate() != null)
            details.setStartDate(request.getStartDate());
        if (request.getDueDate() != null)
            details.setDueDate(request.getDueDate());
        if (request.getCompletionDate() != null)
            details.setCompletionDate(request.getCompletionDate());
        return taskRepository.save(task);
    }
    public void deleteTask(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (!task.getUserEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this task");
        }
        if (task.getTaskDetails().isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task is already deleted");
        }
        task.getTaskDetails().setDeleted(true);
        task.getTaskDetails().setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }
    public void restoreTask(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (!task.getUserEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to restore this task");
        }
        if (!task.getTaskDetails().isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task is not deleted");
        }
        task.getTaskDetails().setDeleted(false);
        task.getTaskDetails().setDeletedAt(null);
        taskRepository.save(task);
    }
}
