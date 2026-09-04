package com.blooddonation.backend.controller;

import com.blooddonation.backend.model.BloodRequest;
import com.blooddonation.backend.repository.BloodRequestRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blood-requests")
@CrossOrigin(origins = "*")
public class BloodRequestController {

    private final BloodRequestRepository bloodRequestRepository;

    public BloodRequestController(
            BloodRequestRepository bloodRequestRepository) {

        this.bloodRequestRepository = bloodRequestRepository;
    }


    // =========================================================
    // SUBMIT BLOOD REQUEST
    // =========================================================

    @PostMapping
public ResponseEntity<?> submitBloodRequest(
        @RequestBody BloodRequest request) {

    try {

        request.setStatus("Pending");

        if (request.getRequestDate() == null) {
            request.setRequestDate(LocalDateTime.now());
        }

        BloodRequest savedRequest =
                bloodRequestRepository.save(request);

        return ResponseEntity
                .status(201)
                .body(
                        Map.of(
                                "message",
                                "Blood request submitted successfully. Waiting for admin approval.",

                                "id",
                                savedRequest.getId(),

                                "status",
                                savedRequest.getStatus()
                        )
                );

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .internalServerError()
                .body(
                        Map.of(
                                "message",
                                "Blood request submission failed",
                                "error",
                                e.getMessage()
                        )
                );
    }
        }


    // =========================================================
    // GET PENDING REQUESTS
    // =========================================================

    @GetMapping("/pending")
    public ResponseEntity<List<BloodRequest>>
    getPendingRequests() {

        return ResponseEntity.ok(
                bloodRequestRepository
                        .findByStatusIgnoreCase("Pending")
        );
    }


    // =========================================================
    // APPROVE REQUEST
    // =========================================================

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveBloodRequest(
            @PathVariable Long id) {

        return bloodRequestRepository.findById(id)
                .map(request -> {

                    request.setStatus("Approved");

                    bloodRequestRepository.save(request);

                    Map<String, Object> response =
                            new HashMap<>();

                    response.put(
                            "message",
                            "Blood request approved successfully"
                    );

                    response.put(
                            "id",
                            request.getId()
                    );

                    response.put(
                            "hospitalName",
                            request.getHospitalName()
                    );

                    response.put(
                            "patientName",
                            request.getPatientName()
                    );

                    response.put(
                            "bloodGroup",
                            request.getBloodGroup()
                    );

                    response.put(
                            "unitsRequired",
                            request.getUnitsRequired()
                    );

                    response.put(
                            "status",
                            request.getStatus()
                    );

                    return ResponseEntity.ok(response);
                })
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =========================================================
    // REJECT REQUEST
    // =========================================================

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectBloodRequest(
            @PathVariable Long id) {

        return bloodRequestRepository.findById(id)
                .map(request -> {

                    request.setStatus("Rejected");

                    bloodRequestRepository.save(request);

                    Map<String, Object> response =
                            new HashMap<>();

                    response.put(
                            "message",
                            "Blood request rejected successfully"
                    );

                    response.put(
                            "id",
                            request.getId()
                    );

                    response.put(
                            "hospitalName",
                            request.getHospitalName()
                    );

                    response.put(
                            "patientName",
                            request.getPatientName()
                    );

                    response.put(
                            "status",
                            request.getStatus()
                    );

                    return ResponseEntity.ok(response);
                })
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =========================================================
    // GET APPROVED REQUESTS
    // =========================================================

    @GetMapping("/approved")
    public ResponseEntity<List<BloodRequest>>
    getApprovedRequests() {

        return ResponseEntity.ok(
                bloodRequestRepository
                        .findByStatusIgnoreCase("Approved")
        );
    }


    // =========================================================
    // GET ALL REQUESTS
    // =========================================================

    @GetMapping("/all")
    public ResponseEntity<List<BloodRequest>>
    getAllRequests() {

        return ResponseEntity.ok(
                bloodRequestRepository.findAll()
        );
    }


    // =========================================================
    // GET REQUEST BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequest(
            @PathVariable Long id) {

        return bloodRequestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }
    @PutMapping("/{id}")
public ResponseEntity<?> updateBloodRequest(
        @PathVariable Long id,
        @RequestBody BloodRequest requestDetails) {

    return bloodRequestRepository.findById(id)
            .map(request -> {

                request.setHospitalName(
                        requestDetails.getHospitalName()
                );

                request.setPatientName(
                        requestDetails.getPatientName()
                );

                request.setAge(
                        requestDetails.getAge()
                );

                request.setGender(
                        requestDetails.getGender()
                );

                request.setPatientStatus(
                        requestDetails.getPatientStatus()
                );

                request.setBloodGroup(
                        requestDetails.getBloodGroup()
                );

                request.setUnitsRequired(
                        requestDetails.getUnitsRequired()
                );

                request.setCity(
                        requestDetails.getCity()
                );

                request.setPhone(
                        requestDetails.getPhone()
                );

                request.setReason(
                        requestDetails.getReason()
                );

                BloodRequest updated =
                        bloodRequestRepository.save(request);

                return ResponseEntity.ok(updated);
            })
            .orElse(
                    ResponseEntity.notFound().build()
            );
}


@DeleteMapping("/{id}")
public ResponseEntity<?> deleteBloodRequest(
        @PathVariable Long id) {

    if (!bloodRequestRepository.existsById(id)) {

        return ResponseEntity
                .notFound()
                .build();
    }

    bloodRequestRepository.deleteById(id);

    return ResponseEntity.ok(
            Map.of(
                    "message",
                    "Blood request deleted successfully"
            )
    );
}
}