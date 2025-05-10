package com.javaProjects.hospital_management.transformer;

import com.javaProjects.hospital_management.dto.request.DoctorRequest;
import com.javaProjects.hospital_management.dto.response.DoctorResponse;
import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.model.Speciality;
import com.javaProjects.hospital_management.model.User;

// Removed unused imports: import com.javaProjects.hospital_management.repository.SpecialityRepository;
// Removed unused imports: import lombok.RequiredArgsConstructor;
// Removed unused imports: import org.springframework.beans.factory.annotation.Autowired;

// @RequiredArgsConstructor // Removed - not needed for a class with only static methods
public class DoctorTransformer {

    public static Doctor doctorRequestToDoctor(DoctorRequest doctorRequest, User user, Speciality speciality) {
        return Doctor.builder()
                .fullName(doctorRequest.getFullName())
                .phoneNumber(doctorRequest.getPhone())
                .specialization(speciality) // Speciality object is passed directly
                .consultationFee(doctorRequest.getConsultationFee())
                .user(user) // User object is passed directly
                .build();
    }

    public static DoctorResponse doctorToDoctorResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .doctorId(doctor.getDoctorId()) // Added doctorId for completeness
                .fullName(doctor.getFullName())
                .phoneNumber(doctor.getPhoneNumber())
                // Added null check for specialization before accessing its name
                .specialization(doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : null)
                // Added email from associated User for completeness, with null check
                .email(doctor.getUser() != null ? doctor.getUser().getEmail() : null)
                .build();
    }
}