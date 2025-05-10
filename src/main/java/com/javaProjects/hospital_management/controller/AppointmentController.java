package com.javaProjects.hospital_management.controller;

import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import com.javaProjects.hospital_management.dto.request.AppointmentRequest;
import com.javaProjects.hospital_management.dto.response.AppointmentResponse;
import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<Appointment> bookAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        Appointment appointment = appointmentService.bookAppointment(appointmentRequest);
        return new ResponseEntity<>(appointment, HttpStatus.CREATED);
    }

    @GetMapping("/pending")
    public List<AppointmentResponse> getAllPendingAppoints() {
        return appointmentService.getAllAppointmentsByStatus(AppointmentStatus.PENDING);
    }

    @GetMapping("/status")
    public List<AppointmentResponse> getAppointmentsByStatus(@RequestParam("status") AppointmentStatus status) {
        return appointmentService.getAllAppointmentsByStatus(status);
    }
}
