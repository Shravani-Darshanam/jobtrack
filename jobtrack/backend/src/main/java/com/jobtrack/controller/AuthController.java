package com.jobtrack.controller;
import com.jobtrack.dto.AuthRequest;
import com.jobtrack.entity.User;
import com.jobtrack.repository.UserRepository;
import com.jobtrack.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) { this.users = users; this.encoder = encoder; this.jwt = jwt; }

    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@Valid @RequestBody AuthRequest r) {
        String email = r.email().trim().toLowerCase();
        if (users.findByEmail(email).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        users.save(new User(email, encoder.encode(r.password())));
        return Map.of("token", jwt.generate(email), "email", email);
    }
    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody AuthRequest r) {
        String email = r.email().trim().toLowerCase();
        users.findByEmail(email).filter(u -> encoder.matches(r.password(), u.getPasswordHash()))
             .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return Map.of("token", jwt.generate(email), "email", email);
    }
}
