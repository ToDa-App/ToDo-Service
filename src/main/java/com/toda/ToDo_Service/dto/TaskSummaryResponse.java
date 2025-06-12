package com.toda.ToDo_Service.dto;

import com.toda.ToDo_Service.entity.TaskDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "TaskSummaryResponse", description = "Response DTO summarizing key details of a task")
public class TaskSummaryResponse {
    @Schema(description = "Task title", example = "Write report")
    private String title;
    @Schema(description = "Priority level of the task", example = "MEDIUM")
    private TaskDetails.Priority priority;
    @Schema(description = "Current status of the task", example = "PENDING")
    private TaskDetails.Status status;
}
