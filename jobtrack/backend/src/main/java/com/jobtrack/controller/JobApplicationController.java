package com.jobtrack.controller;
import com.jobtrack.dto.*;
import com.jobtrack.entity.*;
import com.jobtrack.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {
    private final JobApplicationService service;
    public JobApplicationController(JobApplicationService service) { this.service = service; }

    @GetMapping
    public PageResponse<JobApplication> list(Principal p, @RequestParam(required = false) ApplicationStatus status,
                                             @RequestParam(required = false) JobType jobType, @RequestParam(required = false) String q,
                                             @RequestParam(defaultValue = "desc") String sort,
                                             @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.search(p.getName(), status, jobType, q, sort, page, size);
    }
    @GetMapping("/stats") public Map<String, Long> stats(Principal p) { return service.stats(p.getName()); }
    @GetMapping("/{id}") public JobApplication get(Principal p, @PathVariable Long id) { return service.get(p.getName(), id); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public JobApplication create(Principal p, @Valid @RequestBody ApplicationRequest r) { return service.create(p.getName(), r); }

    @PutMapping("/{id}")
    public JobApplication update(Principal p, @PathVariable Long id, @Valid @RequestBody ApplicationRequest r) { return service.update(p.getName(), id, r); }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Principal p, @PathVariable Long id) { service.delete(p.getName(), id); }
}
