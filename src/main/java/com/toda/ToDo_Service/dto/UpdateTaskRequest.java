package com.toda.ToDo_Service.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
@Getter
@Setter
@Schema(name = "UpdateTaskRequest", description = "Request DTO for updating existing task details")
public class UpdateTaskRequest {
    @Schema(description = "New task title", example = "Buy groceries")
    private String title;
    @Schema(description = "New status of the task", example = "PENDING")
    private String status;
    @Schema(description = "New priority of the task", example = "HIGH")
    private String priority;
    @Schema(description = "Updated description of the task", example = "Pick up milk, eggs, and bread")
    private String description;
    @Schema(description = "New start date for the task", type = "string", format = "date", example = "2025-06-10")
    private LocalDate startDate;
    @Schema(description = "New due date for the task", type = "string", format = "date", example = "2025-06-15")
    private LocalDate dueDate;
    @Schema(description = "New completion date for the task", type = "string", format = "date", example = "2025-06-14")
    private LocalDate completionDate;
}
