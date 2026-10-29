package com.jobtrack.repository;
import com.jobtrack.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    long countByUser(User user);
    long countByUserAndStatus(User user, ApplicationStatus status);
    Optional<JobApplication> findByIdAndUser(Long id, User user);

    @Query("SELECT a FROM JobApplication a WHERE a.user = :user AND (:status IS NULL OR a.status = :status) "
         + "AND (:jobType IS NULL OR a.jobType = :jobType) "
         + "AND (LOWER(a.companyName) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(a.jobTitle) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<JobApplication> search(@Param("user") User user, @Param("status") ApplicationStatus status,
                                @Param("jobType") JobType jobType, @Param("q") String q, Pageable pageable);
}
