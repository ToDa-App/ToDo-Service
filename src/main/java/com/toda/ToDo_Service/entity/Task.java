package com.toda.ToDo_Service.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
<<<<<<< HEAD
    @Column(name = "user_id", nullable = false)
    private Long userId;
=======
    @Column(name = "user_email", nullable = false)
    private String userEmail;
>>>>>>> a736555 (create task API)
    @Column(nullable = false)
    private String title;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id", referencedColumnName = "id")
    private TaskDetails taskDetails;
}
