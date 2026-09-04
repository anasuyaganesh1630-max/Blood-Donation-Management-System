package com.blooddonation.backend.dto;

import java.time.LocalDateTime;

public class PendingRegistrationResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private String status;

    private Integer age;
    private String gender;
    private String bloodGroup;
    private String city;
    private String phone;

    public PendingRegistrationResponse(
            Long id,
            String name,
            String email,
            String role,
            LocalDateTime createdAt,
            String status,
            Integer age,
            String gender,
            String bloodGroup,
            String city,
            String phone) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.status = status;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public Integer getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }
}