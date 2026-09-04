package com.blooddonation.backend.controller;

import com.blooddonation.backend.model.User;
import com.blooddonation.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*")
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> data) {

        String email = data.get("email");
        String password = data.get("password");

        if (email == null || password == null) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Email and password are required"
                    )
            );
        }

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .status(401)
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid email or password"
                            )
                    );
        }

        if (!password.equals(user.getPassword())) {

            return ResponseEntity
                    .status(401)
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid email or password"
                            )
                    );
        }

        if ("Pending".equalsIgnoreCase(user.getStatus())) {

            return ResponseEntity
                    .status(403)
                    .body(
                            Map.of(
                                    "message",
                                    "Your registration is still pending approval"
                            )
                    );
        }

        if ("Rejected".equalsIgnoreCase(user.getStatus())) {

            return ResponseEntity
                    .status(403)
                    .body(
                            Map.of(
                                    "message",
                                    "Your registration has been rejected"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Login successful",

                        "id",
                        user.getId(),

                        "name",
                        user.getName(),

                        "email",
                        user.getEmail(),

                        "role",
                        user.getRole(),

                        "status",
                        user.getStatus()
                )
        );
    }
}