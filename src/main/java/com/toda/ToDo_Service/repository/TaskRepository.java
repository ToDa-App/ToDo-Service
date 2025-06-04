package com.toda.ToDo_Service.repository;

import com.toda.ToDo_Service.entity.Task;
import com.toda.ToDo_Service.entity.TaskDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository <Task, Long> {
}
