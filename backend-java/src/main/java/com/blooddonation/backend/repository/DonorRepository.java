package com.blooddonation.backend.repository;

import com.blooddonation.backend.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DonorRepository extends JpaRepository<Donor, Long> {

    Optional<Donor> findByEmail(String email);

    @Query("""
        SELECT d.bloodGroup, COUNT(d)
        FROM Donor d
        GROUP BY d.bloodGroup
        ORDER BY d.bloodGroup
    """)
    List<Object[]> getBloodGroupStatistics();

    @Query("""
        SELECT d.city, COUNT(d)
        FROM Donor d
        GROUP BY d.city
        ORDER BY d.city
    """)
    List<Object[]> getCityStatistics();
}
