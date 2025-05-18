package com.javaProjects.hospital_management.repository;

import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.model.Billing;
import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {
    boolean existsByDoctor(Doctor doctor);
    boolean existsByPatient(Patient patient);

    Optional<Billing> findByAppointment(Appointment appointment);
}
