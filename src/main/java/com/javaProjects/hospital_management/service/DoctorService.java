package com.javaProjects.hospital_management.service;

import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import com.javaProjects.hospital_management.dto.response.AppointmentResponse;
import com.javaProjects.hospital_management.dto.response.DoctorResponse;
import com.javaProjects.hospital_management.exception.ResourceNotFound;
import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.repository.AppointmentRepository;
import com.javaProjects.hospital_management.repository.DoctorRepository;
import com.javaProjects.hospital_management.transformer.AppointmentTransformer;
import com.javaProjects.hospital_management.transformer.DoctorTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public List<DoctorResponse> getAllDoctorResponses() {
        return doctorRepository.findAll().stream().map(DoctorTransformer::doctorToDoctorResponse).toList();
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId).orElseThrow(
                () -> new ResourceNotFound("No Doctor with Id: "+ doctorId)
        );
    }

    public List<AppointmentResponse> getScheduledAppointments(Long doctorId) {
        return appointmentRepository.findByDoctor_DoctorIdAndStatus(doctorId, AppointmentStatus.SCHEDULED)
                .stream()
                .map(AppointmentTransformer::appointmentToAppointmentResponse)
                .collect(Collectors.toList());
    }
}
