package com.toda.ToDo_Service.repository;

import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskRepository extends JpaRepository <Task, Long> {
     Page<Task> findByUserEmailAndTaskDetailsDeletedFalse(String userEmail, Pageable pageable);
    Page<Task> findByUserEmailAndTaskDetailsStatusAndTaskDetailsDeletedFalse(String userEmail, TaskDetails.Status status, Pageable pageable);
    Page<Task> findByUserEmailAndTaskDetailsPriorityAndTaskDetailsDeletedFalse(String userEmail, TaskDetails.Priority priority, Pageable pageable);
    Page<Task> findByUserEmailAndTaskDetailsStatusAndTaskDetailsPriorityAndTaskDetailsDeletedFalse(String userEmail, TaskDetails.Status status, TaskDetails.Priority priority, Pageable pageable);
    Page<Task> findByUserEmailAndTaskDetails_DeletedTrue(String userEmail, Pageable pageable);
}
