package com.javaProjects.hospital_management.controller;

import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final AppointmentService appointmentService;

    @GetMapping("/appointments")
    public ResponseEntity<?> getMyAppointments() {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments());
    }

    @PutMapping("/appointments/{appointmentId}/complete")
    public ResponseEntity<?> completeAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(appointmentId, AppointmentStatus.COMPLETED));
    }

    @GetMapping("/appointments/scheduled")
    public ResponseEntity<?> getScheduledAppointments() {
        return ResponseEntity.ok(appointmentService.getDoctorScheduledAppointments());
    }
}
