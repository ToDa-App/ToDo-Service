package com.toda.ToDo_Service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "TaskRequest", description = "Request DTO for creating a new task with validation rules")
public class TaskRequest {
    public interface OnCreate {}
    @NotBlank(message = "Title of task is required", groups = {OnCreate.class})
    @Schema(description = "Title of the new task", example = "Finish documentation", required = true)
    private String title;
    @Schema(description = "Detailed description of the task", example = "Complete the API documentation for Task module", defaultValue = "Empty")
    private String description="Empty";
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Start date of the task", type = "string", format = "date", example = "2025-06-10")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Due date for the task", type = "string", format = "date", example = "2025-06-15")
    private LocalDate dueDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Completion date of the task (optional)", type = "string", format = "date", example = "2025-06-14")
    private LocalDate completionDate;
    @NotNull(message = "Priority is required", groups = {OnCreate.class})
    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Priority must be LOW, MEDIUM, or HIGH", groups = {OnCreate.class})
    @Schema(description = "Priority level (LOW, MEDIUM, HIGH)", example = "MEDIUM", required = true)
    private String priority = "MEDIUM"; // default
    @NotNull(message = "Status is required", groups = {OnCreate.class})
    @Pattern(regexp = "PENDING|COMPLETED|OVERDUE", message = "Status must be PENDING, COMPLETED, or OVERDUE", groups = {OnCreate.class})
    @Schema(description = "Current status (PENDING, COMPLETED, OVERDUE)", example = "PENDING", required = true)
    private String status = "PENDING";
    @AssertTrue(message = "Start date must be before or equal to due date", groups = {OnCreate.class})
    @Schema(description = "Validation: startDate <= dueDate")
    public boolean isValidDateRange() {
        if (startDate == null || dueDate == null) {
            return true;
        }
        return !startDate.isAfter(dueDate);
    }
    @AssertTrue(message = "Completion date must be after or equal to start date", groups = {OnCreate.class})
    @Schema(description = "Validation: completionDate >= startDate")
    public boolean isValidCompletionDate() {
        if (completionDate == null || startDate == null) {
            return true;
        }
        return !completionDate.isBefore(startDate);
    }
}
