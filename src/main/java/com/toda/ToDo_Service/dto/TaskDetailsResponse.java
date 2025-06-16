package com.toda.ToDo_Service.dto;

import com.toda.ToDo_Service.entity.TaskDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "TaskDetailsResponse", description = "Response DTO containing full task details")
public class TaskDetailsResponse {
    private Long id;
    @Schema(description = "Task title", example = "Plan meeting agenda")
    private String title;
    @Schema(description = "Task description", example = "Discuss quarterly goals and budget allocations")
    private String description;
    @Schema(description = "Priority level of the task", example = "HIGH")
    private TaskDetails.Priority priority;
    @Schema(description = "Current status of the task", example = "OVERDUE")
    private TaskDetails.Status status;
    @Schema(description = "Start date of the task", type = "string", format = "date", example = "2025-06-01")
    private LocalDate startDate;
    @Schema(description = "Due date for the task", type = "string", format = "date", example = "2025-06-05")
    private LocalDate dueDate;
    @Schema(description = "Completion date of the task", type = "string", format = "date", example = "2025-06-04")
    private LocalDate completionDate;
}
