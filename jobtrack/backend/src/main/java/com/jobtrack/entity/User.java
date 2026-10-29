package com.jobtrack.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 150) private String email;
    @Column(nullable = false) private String passwordHash;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
    protected User() {}
    public User(String email, String passwordHash) { this.email = email; this.passwordHash = passwordHash; }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
}
