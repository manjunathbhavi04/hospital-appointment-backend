package com.javaProjects.hospital_management.controller;

import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import com.javaProjects.hospital_management.dto.request.AssignDoctorRequest;
import com.javaProjects.hospital_management.dto.response.AppointmentResponse;
import com.javaProjects.hospital_management.dto.response.StaffResponse;
import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.service.AppointmentService;
import com.javaProjects.hospital_management.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;
    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @PostMapping("/assign/doctor/{appointmentId}")
    public ResponseEntity<?> assignDoctorToAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long doctorId) {
        return ResponseEntity.ok(appointmentService.assignDoctor(appointmentId, doctorId));
    }

    @PostMapping("/assign/doctor")
    public ResponseEntity<AppointmentResponse> assignDoctor(@RequestBody AssignDoctorRequest assignDoctorRequest) {
        return new ResponseEntity<>(staffService.assignDoctorManually(assignDoctorRequest.getAppointmentId(), assignDoctorRequest.getDoctorId()), HttpStatus.OK);
    }

    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(appointmentId, status));
    }

    @GetMapping("/appointments/pending")
    public ResponseEntity<List<AppointmentResponse>> getPendingAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointmentsByStatus(AppointmentStatus.PENDING));
    }
}
