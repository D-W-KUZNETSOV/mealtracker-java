package com.e.mealtracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // например, dmitriy

    @Column(nullable = false)
    private String password; // будет храниться как хеш!

    // Можно добавить role, если планируешь админку
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
}

