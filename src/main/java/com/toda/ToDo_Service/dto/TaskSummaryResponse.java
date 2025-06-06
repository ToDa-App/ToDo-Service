package com.toda.ToDo_Service.dto;

import com.toda.ToDo_Service.entity.TaskDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskSummaryResponse {
    private String title;
    private TaskDetails.Priority priority;
    private TaskDetails.Status status;
}
