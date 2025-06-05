package com.toda.ToDo_Service.repository;

import com.toda.ToDo_Service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskRepository extends JpaRepository <Task, Long> {
}
