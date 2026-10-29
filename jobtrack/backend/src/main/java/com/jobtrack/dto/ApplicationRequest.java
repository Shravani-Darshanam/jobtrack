package com.jobtrack.dto;
import com.jobtrack.entity.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ApplicationRequest(
    @NotBlank @Size(max = 100) String companyName,
    @NotBlank @Size(max = 100) String jobTitle,
    @NotNull JobType jobType,
    @NotBlank @Size(max = 100) String location,
    @NotNull @PastOrPresent LocalDate applicationDate,
    @NotNull ApplicationStatus status,
    @Size(max = 500) String jobUrl,
    LocalDate followUpDate,
    @Size(max = 1000) String notes) {}
