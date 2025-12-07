package com.example.splitwise.controllers;

import com.example.splitwise.model.User;
import com.example.splitwise.repo.UserRepo;
//import com.example.splitwise.JwtService;
import com.example.splitwise.service.EmailService;
import com.example.splitwise.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final EmailService emailService;


    private final AuthenticationManager authManager;
    private final UserRepo userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(EmailService emailService, AuthenticationManager authManager, UserRepo userRepo,
                          PasswordEncoder encoder, JwtService jwt) {
        this.emailService = emailService;
        this.authManager = authManager;
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwt = jwt;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String username = body.getOrDefault("username", email);

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "email_and_password_required"));
        }
        if (userRepo.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "email_exists"));
        }

        User u = new User();
        u.setEmail(email);
        u.setUsername(username);
        u.setPassword(encoder.encode(password));
        u.setEmailVerified(false);

        String token = UUID.randomUUID().toString();
        u.setVerificationToken(token);
        u.setVerificationExpiresAt(LocalDateTime.now().plusHours(24));

        userRepo.save(u);

        emailService.sendVerificationEmail(
                u.getEmail(),
                u.getUsername(),
                token
        );

        return ResponseEntity.ok(
                Map.of(
                        "message", "verification_email_sent",
                        "email", email
                )
        );
    }
//    @PostMapping("/signup")
//    public ResponseEntity<?> signup(@RequestBody Map<String,String> body) {
//        String email = body.get("email");
//        String password = body.get("password");
//        String username = body.getOrDefault("username", email);
//
//        if (email == null || password == null) {
//            return ResponseEntity.badRequest().body(Map.of("error", "email_and_password_required"));
//        }
//        if (userRepo.findByEmail(email).isPresent()) {
//            return ResponseEntity.status(409).body(Map.of("error", "email_exists"));
//        }
//
//        User u = new User();
//        u.setEmail(email);
//        u.setUsername(username);
//        u.setPassword(encoder.encode(password));
//        u.setEmailVerified(false);
//        userRepo.save(u);
//
//        String token = jwt.generateToken(u.getEmail());
//        return ResponseEntity.ok(Map.of("token", token));
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error","email_and_password_required"));
        }

        // Checkng if user exists
        var opt = userRepo.findByEmail(email);
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error","invalid_credentials"));
        }
        User u = opt.get();

        //  Block if not verified
        if (!u.isEmailVerified()) {
            return ResponseEntity.status(403).body(Map.of("error","email_not_verified"));
        }

        //  Authenticate password
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("error","invalid_credentials"));
        }

        //  Generate JWT
        String token = jwt.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token));
    }


//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
//        String email = body.get("email");
//        String password = body.get("password");
//
//        if (email == null || password == null) {
//            return ResponseEntity.badRequest().body(Map.of("error","email_and_password_required"));
//        }
//
//        try {
//            Authentication auth = authManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(email, password)
//            );
//        } catch (BadCredentialsException ex) {
//            return ResponseEntity.status(401).body(Map.of("error","invalid_credentials"));
//        }
//
//        String token = jwt.generateToken(email);
//        return ResponseEntity.ok(Map.of("token", token));
//    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        var opt = userRepo.findByVerificationToken(token);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or already used link");
        }

        User u = opt.get();

        if (u.getVerificationExpiresAt() != null &&
                u.getVerificationExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Verification link expired");
        }

        u.setEmailVerified(true);
        u.setVerificationToken(null);
        u.setVerificationExpiresAt(null);
        userRepo.save(u);

        // auto-login: redirect to frontend with JWT
        String jwtToken = jwt.generateToken(u.getEmail());
        String redirectUrl = "https://spliteaseapp.atul.codes/email-verified?token=" + jwtToken;

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

}
