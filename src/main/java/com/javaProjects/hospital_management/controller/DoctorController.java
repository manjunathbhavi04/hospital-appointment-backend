package com.javaProjects.hospital_management.controller;

import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import com.javaProjects.hospital_management.dto.request.DoctorUpdate;
import com.javaProjects.hospital_management.dto.response.AppointmentResponse;
import com.javaProjects.hospital_management.dto.response.DoctorResponse;
import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.service.AppointmentService;
import com.javaProjects.hospital_management.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        return new ResponseEntity<>(doctorService.getAllDoctors1(),HttpStatus.OK);
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> getMyAppointments() {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments());
    }

    @PutMapping("/appointments/{appointmentId}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(appointmentId, AppointmentStatus.COMPLETED));
    }

    @PutMapping("update/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(@PathVariable Long id, @RequestBody DoctorUpdate doctor) {
        System.out.println("Was trying to update doctor");
        return new ResponseEntity<>(doctorService.updateDoctor(id, doctor), HttpStatus.OK);
    }

    @GetMapping("/appointments/scheduled")
    public ResponseEntity<?> getScheduledAppointments() {
        return ResponseEntity.ok(appointmentService.getDoctorScheduledAppointments());
    }
}
