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

    @Column(unique = true, nullable = true) // nullable=true пока, чтобы старые записи не ломались
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;

    public void setProfile(UserProfile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setUser(this);
        }
    }

}

