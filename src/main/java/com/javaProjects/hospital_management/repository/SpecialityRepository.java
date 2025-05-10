package com.javaProjects.hospital_management.repository;

import com.javaProjects.hospital_management.model.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
    Speciality findByName(String name);
}
