package com.jobtrack.entity;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100) private String companyName;
    @Column(nullable = false, length = 100) private String jobTitle;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private JobType jobType;
    @Column(nullable = false, length = 100) private String location;
    @Column(nullable = false) private LocalDate applicationDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private ApplicationStatus status;
    @Column(length = 500) private String jobUrl;
    @Column(length = 1000) private String notes;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") @JsonIgnore private User user;
    private LocalDate followUpDate;
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true) @OrderBy("changedAt ASC")
    private List<StatusChange> statusHistory = new ArrayList<>();
    public User getUser() { return user; }
    public void setUser(User u) { user = u; }
    public LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDate d) { followUpDate = d; }
    public List<StatusChange> getStatusHistory() { return statusHistory; }
    public void addHistory(ApplicationStatus s) { statusHistory.add(new StatusChange(this, s)); }

    @PrePersist void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String v) { companyName = v; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String v) { jobTitle = v; }
    public JobType getJobType() { return jobType; }
    public void setJobType(JobType v) { jobType = v; }
    public String getLocation() { return location; }
    public void setLocation(String v) { location = v; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate v) { applicationDate = v; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus v) { status = v; }
    public String getJobUrl() { return jobUrl; }
    public void setJobUrl(String v) { jobUrl = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { notes = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
