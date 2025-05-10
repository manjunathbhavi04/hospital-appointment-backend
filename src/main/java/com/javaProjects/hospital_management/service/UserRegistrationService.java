package com.javaProjects.hospital_management.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Added for transaction management

import com.javaProjects.hospital_management.Enum.Role;
import com.javaProjects.hospital_management.dto.request.DoctorRequest;
import com.javaProjects.hospital_management.dto.request.StaffRequest;
import com.javaProjects.hospital_management.dto.response.DoctorResponse;
import com.javaProjects.hospital_management.dto.response.StaffResponse;
import com.javaProjects.hospital_management.exception.ResourceNotFound; // Added for consistent error handling
import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.model.Speciality;
import com.javaProjects.hospital_management.model.Staff;
import com.javaProjects.hospital_management.model.User;
import com.javaProjects.hospital_management.repository.DoctorRepository;
import com.javaProjects.hospital_management.repository.SpecialityRepository;
import com.javaProjects.hospital_management.repository.StaffRepository;
import com.javaProjects.hospital_management.repository.UserRepository;
import com.javaProjects.hospital_management.transformer.DoctorTransformer;
import com.javaProjects.hospital_management.transformer.StaffTransformer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final StaffRepository staffRepository;
    private final SpecialityRepository specialityRepository; // Injected for doctor registration

    @Transactional // Ensures all DB operations are atomic
    public DoctorResponse registerDoctor(DoctorRequest doctorRequest) {
        // Check for duplicate username and email before saving
        if (userRepository.findByUsername(doctorRequest.getUsername()).isPresent()) {
            throw new ResourceNotFound("Username '" + doctorRequest.getUsername() + "' already exists. Please choose a different username.");
            // Ideally, you'd have a more specific exception like UserAlreadyExistsException
        }
        if (userRepository.findByEmail(doctorRequest.getEmail()).isPresent()) {
            throw new ResourceNotFound("Email '" + doctorRequest.getEmail() + "' already exists. Please use a different email.");
        }

        User user = User.builder()
                .username(doctorRequest.getUsername())
                .password(passwordEncoder.encode(doctorRequest.getPassword()))
                .email(doctorRequest.getEmail())
                .role(Role.DOCTOR)
                .build();

        userRepository.save(user); // Save the User entity first

        // Handle Speciality: find existing or create new
        // Assuming specialityRepository.findByName returns Speciality or null
        Speciality speciality = specialityRepository.findByName(doctorRequest.getSpecialization());

        if(speciality == null) {
            speciality = Speciality.builder()
                    .name(doctorRequest.getSpecialization())
                    .build();
            specialityRepository.save(speciality); // Save new Speciality if it doesn't exist
        }

        // Transform and save the Doctor profile, linking it to the User and Speciality
        Doctor doctor = DoctorTransformer.doctorRequestToDoctor(doctorRequest, user, speciality);
        doctorRepository.save(doctor);

        // Transform and return the DoctorResponse DTO
        return DoctorTransformer.doctorToDoctorResponse(doctor);
    }

    @Transactional // Ensures all DB operations are atomic
    public StaffResponse registerStaff(StaffRequest staffRequest) {
        // CRITICAL FIX: Check for duplicate username and email before saving
        if (userRepository.findByUsername(staffRequest.getUsername()).isPresent()) {
            throw new ResourceNotFound("Username '" + staffRequest.getUsername() + "' already exists. Please choose a different username.");
        }
        if (userRepository.findByEmail(staffRequest.getEmail()).isPresent()) {
            throw new ResourceNotFound("Email '" + staffRequest.getEmail() + "' already exists. Please use a different email.");
        }

        User user = User.builder()
                .username(staffRequest.getUsername())
                .password(passwordEncoder.encode(staffRequest.getPassword()))
                .email(staffRequest.getEmail())
                .role(Role.STAFF)
                .build();

        userRepository.save(user); // Save the User entity first

        // Transform and save the Staff profile, linking it to the User
        Staff staff = StaffTransformer.staffRequestToStaff(staffRequest, user);
        staffRepository.save(staff);

        // Transform and return the StaffResponse DTO
        return StaffTransformer.staffToStaffResponse(staff);
    }
}