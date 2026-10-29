package com.jobtrack.service;
import com.jobtrack.dto.*;
import com.jobtrack.entity.*;
import com.jobtrack.exception.NotFoundException;
import com.jobtrack.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class JobApplicationService {
    private final JobApplicationRepository repo;
    private final UserRepository users;
    public JobApplicationService(JobApplicationRepository repo, UserRepository users) { this.repo = repo; this.users = users; }

    private User me(String email) { return users.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found")); }

    public PageResponse<JobApplication> search(String email, ApplicationStatus status, JobType type, String q, String sort, int page, int size) {
        Sort.Direction dir = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable p = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50), Sort.by(dir, "applicationDate").and(Sort.by("id")));
        Page<JobApplication> r = repo.search(me(email), status, type, q == null ? "" : q.trim(), p);
        return new PageResponse<>(r.getContent(), r.getNumber(), r.getTotalPages(), r.getTotalElements());
    }
    public JobApplication get(String email, Long id) {
        return repo.findByIdAndUser(id, me(email)).orElseThrow(() -> new NotFoundException("Application " + id + " not found"));
    }
    public JobApplication create(String email, ApplicationRequest r) {
        JobApplication a = apply(new JobApplication(), r);
        a.setUser(me(email)); a.addHistory(r.status());
        return repo.save(a);
    }
    public JobApplication update(String email, Long id, ApplicationRequest r) {
        JobApplication a = get(email, id);
        ApplicationStatus old = a.getStatus();
        apply(a, r);
        if (old != r.status()) a.addHistory(r.status());
        return repo.save(a);
    }
    public void delete(String email, Long id) { repo.delete(get(email, id)); }

    public Map<String, Long> stats(String email) {
        User u = me(email);
        Map<String, Long> m = new LinkedHashMap<>();
        m.put("total", repo.countByUser(u));
        for (ApplicationStatus s : ApplicationStatus.values()) m.put(s.name(), repo.countByUserAndStatus(u, s));
        return m;
    }
    private JobApplication apply(JobApplication a, ApplicationRequest r) {
        a.setCompanyName(r.companyName().trim()); a.setJobTitle(r.jobTitle().trim());
        a.setJobType(r.jobType()); a.setLocation(r.location().trim());
        a.setApplicationDate(r.applicationDate()); a.setStatus(r.status()); a.setFollowUpDate(r.followUpDate());
        a.setJobUrl(r.jobUrl() == null || r.jobUrl().isBlank() ? null : r.jobUrl().trim());
        a.setNotes(r.notes() == null || r.notes().isBlank() ? null : r.notes().trim());
        return a;
    }
}
