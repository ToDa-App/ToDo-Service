package com.toda.ToDo_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "task_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;
    private String description="empty";
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completionDate;
    @Column(nullable = false)
    private boolean deleted = false;
    private LocalDateTime deletedAt;
    public enum Status {
        PENDING,
        COMPLETED,
        OVERDUE
    }
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
}
