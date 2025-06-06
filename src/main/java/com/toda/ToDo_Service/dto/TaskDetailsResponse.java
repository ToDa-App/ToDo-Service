package com.toda.ToDo_Service.dto;

import com.toda.ToDo_Service.entity.TaskDetails;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDetailsResponse {
    private String title;
    private String description;
    private TaskDetails.Priority priority;
    private TaskDetails.Status status;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completionDate;
}
