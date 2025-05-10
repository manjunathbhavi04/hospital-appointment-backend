package com.javaProjects.hospital_management.repository;

import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

//    List<Doctor> findBySpecialization(String specialization);

    Optional<Doctor> findByUser(User user);
}
