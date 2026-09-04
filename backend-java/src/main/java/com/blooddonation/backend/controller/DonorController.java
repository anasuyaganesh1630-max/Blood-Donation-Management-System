package com.blooddonation.backend.controller;

import com.blooddonation.backend.model.Donor;
import com.blooddonation.backend.model.User;
import com.blooddonation.backend.repository.DonorRepository;
import com.blooddonation.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donors")
@CrossOrigin(origins = "*")
public class DonorController {

    private final DonorRepository donorRepository;
    private final UserRepository userRepository;

    public DonorController(
            DonorRepository donorRepository,
            UserRepository userRepository) {

        this.donorRepository = donorRepository;
        this.userRepository = userRepository;
    }


    // ==========================================
    // TEST
    // ==========================================

    @GetMapping("/")
    public ResponseEntity<?> test() {

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Donor API working"
                )
        );
    }


    // ==========================================
    // GET ALL DONORS
    // ==========================================

    @GetMapping("/all")
    public ResponseEntity<List<Donor>> getAllDonors() {

        return ResponseEntity.ok(
                donorRepository.findAll()
        );
    }


    // ==========================================
    // GET APPROVED DONORS
    // ==========================================

    @GetMapping("/approved")
    public ResponseEntity<List<Donor>> getApprovedDonors() {

        return ResponseEntity.ok(
                donorRepository.findAll()
        );
    }


    // ==========================================
    // GET DONOR BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getDonor(
            @PathVariable Long id) {

        return donorRepository.findById(id)
                .map(donor ->
                        ResponseEntity.ok(donor)
                )
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }


    // ==========================================
    // REGISTER DONOR
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<?> registerDonor(
            @RequestBody Map<String, Object> data) {

        try {

            // ------------------------------------------
            // READ FORM DATA
            // ------------------------------------------

            String name =
                    data.get("name") != null
                            ? data.get("name").toString().trim()
                            : null;

            String email =
                    data.get("email") != null
                            ? data.get("email").toString().trim()
                            : null;

            String gender =
                    data.get("gender") != null
                            ? data.get("gender").toString()
                            : null;

            String bloodGroup =
                    data.get("bloodGroup") != null
                            ? data.get("bloodGroup").toString()
                            : null;

            String city =
                    data.get("city") != null
                            ? data.get("city").toString()
                            : null;

            String phone =
                    data.get("phone") != null
                            ? data.get("phone").toString().trim()
                            : null;


            Integer age = null;

            if (data.get("age") != null) {

                age = Integer.valueOf(
                        data.get("age").toString()
                );
            }


            // ------------------------------------------
            // VALIDATION
            // ------------------------------------------

            if (name == null || name.isEmpty()
                    || email == null || email.isEmpty()
                    || age == null
                    || gender == null || gender.isEmpty()
                    || bloodGroup == null || bloodGroup.isEmpty()
                    || city == null || city.isEmpty()
                    || phone == null || phone.isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Please fill all required fields"
                                )
                        );
            }


            // ------------------------------------------
            // CHECK EXISTING EMAIL
            // ------------------------------------------

            if (userRepository.findByEmail(email).isPresent()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Email already registered"
                                )
                        );
            }


            // ------------------------------------------
            // CREATE PENDING USER
            // ------------------------------------------

            User user = new User();

            user.setName(name);
            user.setEmail(email);

            user.setPassword(null);

            user.setRole("donor");

            user.setStatus("Pending");

            user.setCreatedAt(
                    LocalDateTime.now()
            );


            // ------------------------------------------
            // IMPORTANT:
            // SAVE ALL DONOR DETAILS IN USERS TABLE
            // ------------------------------------------

            user.setAge(age);

            user.setGender(gender);

            user.setBloodGroup(bloodGroup);

            user.setCity(city);

            user.setPhone(phone);


            // ------------------------------------------
            // SAVE USER
            // ------------------------------------------

            User savedUser =
                    userRepository.save(user);


            // ------------------------------------------
            // DO NOT SAVE TO DONORS TABLE YET
            //
            // Donor record will be created after
            // admin approves the registration.
            // ------------------------------------------

            return ResponseEntity
                    .status(201)
                    .body(
                            Map.of(
                                    "message",
                                    "Donor registration submitted. Waiting for admin approval.",

                                    "user_id",
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
                                    "Donor registration failed",

                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // ==========================================
    // BLOOD GROUP STATISTICS
    // ==========================================

    @GetMapping("/stats")
    public ResponseEntity<List<Map<String, Object>>>
    getBloodGroupStatistics() {

        List<Object[]> results =
                donorRepository.getBloodGroupStatistics();

        List<Map<String, Object>> response =
                results.stream()
                        .map(row ->
                                Map.of(
                                        "blood_group",
                                        row[0],

                                        "total",
                                        row[1]
                                )
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // CITY STATISTICS
    // ==========================================

    @GetMapping("/citystats")
    public ResponseEntity<List<Map<String, Object>>>
    getCityStatistics() {

        List<Object[]> results =
                donorRepository.getCityStatistics();

        List<Map<String, Object>> response =
                results.stream()
                        .map(row ->
                                Map.of(
                                        "city",
                                        row[0],

                                        "total",
                                        row[1]
                                )
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}")
public ResponseEntity<?> updateDonor(
        @PathVariable Long id,
        @RequestBody Donor donorDetails) {

    return donorRepository.findById(id)
            .map(donor -> {

                donor.setName(donorDetails.getName());
                donor.setEmail(donorDetails.getEmail());
                donor.setBloodGroup(donorDetails.getBloodGroup());
                donor.setAge(donorDetails.getAge());
                donor.setGender(donorDetails.getGender());
                donor.setCity(donorDetails.getCity());
                donor.setPhone(donorDetails.getPhone());

                donorRepository.save(donor);

                return ResponseEntity.ok(donor);
            })
            .orElse(ResponseEntity.notFound().build());
}


@DeleteMapping("/{id}")
public ResponseEntity<?> deleteDonor(
        @PathVariable Long id) {

    if (!donorRepository.existsById(id)) {
        return ResponseEntity.notFound().build();
    }

    donorRepository.deleteById(id);

    return ResponseEntity.ok(
            Map.of(
                    "message",
                    "Donor deleted successfully"
            )
    );
}

}