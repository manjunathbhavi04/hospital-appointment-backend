package com.javaProjects.hospital_management.controller;

import com.javaProjects.hospital_management.dto.request.DoctorRequest;
import com.javaProjects.hospital_management.dto.request.StaffRequest;
import com.javaProjects.hospital_management.dto.response.DoctorResponse;
import com.javaProjects.hospital_management.dto.response.StaffResponse;
import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping("/doctor")
    public ResponseEntity<DoctorResponse> registerDoctor(@RequestBody DoctorRequest doctorRequest) {
        System.out.println("Registering");
        DoctorResponse doctor = userRegistrationService.registerDoctor(doctorRequest);
        return ResponseEntity.ok(doctor);
    }

    @PostMapping("/staff")
    public ResponseEntity<StaffResponse> registerStaff(@RequestBody StaffRequest staffRequest) {
        StaffResponse staff = userRegistrationService.registerStaff(staffRequest);
        return ResponseEntity.ok(staff);
    }
}
