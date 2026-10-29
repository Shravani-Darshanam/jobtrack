package com.jobtrack.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "status_changes")
public class StatusChange {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "application_id") @JsonIgnore private JobApplication application;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private ApplicationStatus status;
    @Column(nullable = false) private LocalDateTime changedAt = LocalDateTime.now();
    protected StatusChange() {}
    public StatusChange(JobApplication a, ApplicationStatus s) { application = a; status = s; }
    public ApplicationStatus getStatus() { return status; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
