package com.blooddonation.backend.controller;

import com.blooddonation.backend.dto.PendingRegistrationResponse;
import com.blooddonation.backend.model.Donor;
import com.blooddonation.backend.model.User;
import com.blooddonation.backend.repository.DonorRepository;
import com.blooddonation.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {

    private final UserRepository userRepository;
    private final DonorRepository donorRepository;

    public RegistrationController(
            UserRepository userRepository,
            DonorRepository donorRepository) {

        this.userRepository = userRepository;
        this.donorRepository = donorRepository;
    }


    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        try {

            user.setStatus("Pending");

            if (user.getCreatedAt() == null) {
                user.setCreatedAt(LocalDateTime.now());
            }

            User savedUser = userRepository.save(user);

            return ResponseEntity
                    .status(201)
                    .body(
                            Map.of(
                                    "message",
                                    "Registration submitted successfully. Waiting for admin approval.",

                                    "id",
                                    savedUser.getId(),

                                    "status",
                                    savedUser.getStatus()
                            )
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Registration failed",
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // GET ALL PENDING REGISTRATIONS
    // =========================================================

    @GetMapping("/pending")
    public ResponseEntity<List<PendingRegistrationResponse>>
    getPendingRegistrations() {

        List<User> pendingUsers =
                userRepository.findByStatusIgnoreCase("Pending");

        List<PendingRegistrationResponse> response =
                pendingUsers.stream()
                        .map(user ->
                                new PendingRegistrationResponse(
                                        user.getId(),
                                        user.getName(),
                                        user.getEmail(),
                                        user.getRole(),
                                        user.getCreatedAt(),
                                        user.getStatus(),
                                        user.getAge(),
                                        user.getGender(),
                                        user.getBloodGroup(),
                                        user.getCity(),
                                        user.getPhone()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // APPROVE REGISTRATION
    // =========================================================

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveRegistration(
            @PathVariable Long id) {

        return userRepository.findById(id)
                .map(user -> {

                    // -----------------------------------------
                    // APPROVE USER
                    // -----------------------------------------

                    user.setStatus("Approved");

                    userRepository.save(user);


                    // -----------------------------------------
                    // IF DONOR, CREATE DONOR RECORD
                    // -----------------------------------------

                    if (user.getRole() != null &&
                            user.getRole().equalsIgnoreCase("donor")) {

                        Donor donor =
                                donorRepository
                                        .findByEmail(user.getEmail())
                                        .orElse(null);


                        // -----------------------------------------
                        // CREATE ONLY IF DONOR DOES NOT EXIST
                        // -----------------------------------------

                        if (donor == null) {

                            donor = new Donor();

                            donor.setName(user.getName());
                            donor.setEmail(user.getEmail());
                            donor.setAge(user.getAge());
                            donor.setGender(user.getGender());
                            donor.setBloodGroup(user.getBloodGroup());
                            donor.setCity(user.getCity());
                            donor.setPhone(user.getPhone());

                            donorRepository.save(donor);

                        } else {

                            // -----------------------------------------
                            // UPDATE EXISTING DONOR DETAILS
                            // -----------------------------------------

                            donor.setName(user.getName());
                            donor.setAge(user.getAge());
                            donor.setGender(user.getGender());
                            donor.setBloodGroup(user.getBloodGroup());
                            donor.setCity(user.getCity());
                            donor.setPhone(user.getPhone());

                            donorRepository.save(donor);
                        }
                    }


                    // -----------------------------------------
                    // RESPONSE
                    // -----------------------------------------

                    Map<String, Object> response =
                            new HashMap<>();

                    response.put(
                            "message",
                            "Registration approved successfully"
                    );

                    response.put("id", user.getId());
                    response.put("name", user.getName());
                    response.put("role", user.getRole());
                    response.put("status", user.getStatus());

                    return ResponseEntity.ok(response);

                })
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =========================================================
    // REJECT REGISTRATION
    // =========================================================

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectRegistration(
            @PathVariable Long id) {

        return userRepository.findById(id)
                .map(user -> {

                    user.setStatus("Rejected");

                    userRepository.save(user);

                    Map<String, Object> response =
                            new HashMap<>();

                    response.put(
                            "message",
                            "Registration rejected successfully"
                    );

                    response.put("id", user.getId());
                    response.put("name", user.getName());
                    response.put("role", user.getRole());
                    response.put("status", user.getStatus());

                    return ResponseEntity.ok(response);

                })
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =========================================================
    // GET APPROVED DONORS
    // =========================================================

    @GetMapping("/donors/approved")
    public ResponseEntity<List<Map<String, Object>>>
    getApprovedDonors() {

        // -----------------------------------------
        // Get approved donor USERS
        // -----------------------------------------

        List<User> approvedUsers =
                userRepository
                        .findByRoleIgnoreCaseAndStatusIgnoreCase(
                                "donor",
                                "Approved"
                        );


        List<Map<String, Object>> response =
                new ArrayList<>();


        // -----------------------------------------
        // Get actual donor details
        // from DONORS table
        // -----------------------------------------

        for (User user : approvedUsers) {

            Donor donor =
                    donorRepository
                            .findByEmail(user.getEmail())
                            .orElse(null);


            // -----------------------------------------
            // DONOR RECORD EXISTS
            // -----------------------------------------

            if (donor != null) {

                Map<String, Object> donorData =
                        new HashMap<>();

                donorData.put("id", donor.getId());
                donorData.put("name", donor.getName());
                donorData.put("age", donor.getAge());
                donorData.put("gender", donor.getGender());
                donorData.put("email", donor.getEmail());
                donorData.put("bloodGroup", donor.getBloodGroup());
                donorData.put("city", donor.getCity());
                donorData.put("phone", donor.getPhone());
                donorData.put("status", "Approved");

                response.add(donorData);

            }

            // -----------------------------------------
            // DONOR RECORD DOES NOT EXIST
            // -----------------------------------------

            else {

                // Create donor record automatically
                // from the User information.

                Donor newDonor = new Donor();

                newDonor.setName(user.getName());
                newDonor.setEmail(user.getEmail());
                newDonor.setAge(user.getAge());
                newDonor.setGender(user.getGender());
                newDonor.setBloodGroup(user.getBloodGroup());
                newDonor.setCity(user.getCity());
                newDonor.setPhone(user.getPhone());

                Donor savedDonor =
                        donorRepository.save(newDonor);


                Map<String, Object> donorData =
                        new HashMap<>();

                donorData.put(
                        "id",
                        savedDonor.getId()
                );

                donorData.put(
                        "name",
                        savedDonor.getName()
                );

                donorData.put(
                        "age",
                        savedDonor.getAge()
                );

                donorData.put(
                        "gender",
                        savedDonor.getGender()
                );

                donorData.put(
                        "email",
                        savedDonor.getEmail()
                );

                donorData.put(
                        "bloodGroup",
                        savedDonor.getBloodGroup()
                );

                donorData.put(
                        "city",
                        savedDonor.getCity()
                );

                donorData.put(
                        "phone",
                        savedDonor.getPhone()
                );

                donorData.put(
                        "status",
                        "Approved"
                );

                response.add(donorData);
            }
        }


        return ResponseEntity.ok(response);
    }


    // =========================================================
    // GET APPROVED HOSPITALS
    // =========================================================

    @GetMapping("/hospitals/approved")
    public ResponseEntity<List<PendingRegistrationResponse>>
    getApprovedHospitals() {

        List<User> hospitals =
                userRepository
                        .findByRoleIgnoreCaseAndStatusIgnoreCase(
                                "hospital",
                                "Approved"
                        );


        List<PendingRegistrationResponse> response =
                hospitals.stream()
                        .map(user ->
                                new PendingRegistrationResponse(
                                        user.getId(),
                                        user.getName(),
                                        user.getEmail(),
                                        user.getRole(),
                                        user.getCreatedAt(),
                                        user.getStatus(),
                                        user.getAge(),
                                        user.getGender(),
                                        user.getBloodGroup(),
                                        user.getCity(),
                                        user.getPhone()
                                )
                        )
                        .toList();


        return ResponseEntity.ok(response);
    }

}
