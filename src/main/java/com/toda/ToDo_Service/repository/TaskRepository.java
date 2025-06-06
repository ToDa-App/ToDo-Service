package com.toda.ToDo_Service.repository;

import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TaskRepository extends JpaRepository <Task, Long> {
    Page<Task> findByUserEmail(String userEmail, Pageable pageable);
    Page<Task> findByUserEmailAndTaskDetailsStatus(String userEmail, TaskDetails.Status status, Pageable pageable);
    Page<Task> findByUserEmailAndTaskDetailsPriority(String userEmail, TaskDetails.Priority priority, Pageable pageable);
    Page<Task> findByUserEmailAndTaskDetailsStatusAndTaskDetailsPriority(String userEmail, TaskDetails.Status status, TaskDetails.Priority priority, Pageable pageable);
}
