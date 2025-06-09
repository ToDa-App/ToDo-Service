package com.toda.ToDo_Service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class UpdateTaskRequest {
    private String title;
    private String status;
    private String priority;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completionDate;
}
