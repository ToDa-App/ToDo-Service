package com.toda.ToDo_Service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Getter
@Setter
public class TaskRequest {
    public interface OnCreate {}
    @NotBlank(message = "Title of task is required", groups = {OnCreate.class})
    private String title;
    private String description="Empty";
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate completionDate;
    @NotNull(message = "Priority is required", groups = {OnCreate.class})
    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Priority must be LOW, MEDIUM, or HIGH", groups = {OnCreate.class})
    private String priority = "MEDIUM"; // default
    @NotNull(message = "Status is required", groups = {OnCreate.class})
    @Pattern(regexp = "PENDING|COMPLETED|OVERDUE", message = "Status must be PENDING, COMPLETED, or OVERDUE", groups = {OnCreate.class})
    private String status = "PENDING";
    @AssertTrue(message = "Start date must be before or equal to due date", groups = {OnCreate.class})
    public boolean isValidDateRange() {
        if (startDate == null || dueDate == null) {
            return true;
        }
        return !startDate.isAfter(dueDate);
    }
    @AssertTrue(message = "Completion date must be after or equal to start date", groups = {OnCreate.class})
    public boolean isValidCompletionDate() {
        if (completionDate == null || startDate == null) {
            return true;
        }
        return !completionDate.isBefore(startDate);
    }
}
