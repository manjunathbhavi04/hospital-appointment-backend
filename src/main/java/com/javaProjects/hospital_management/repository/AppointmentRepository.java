package com.javaProjects.hospital_management.repository;

import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import com.javaProjects.hospital_management.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByStatus(AppointmentStatus appointmentStatus);

    List<Appointment> findByDoctor_DoctorId(Long doctorId);

    List<Appointment> findByDoctor_DoctorIdAndStatus(Long doctorId, AppointmentStatus status);
}
