package com.javaProjects.hospital_management.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotNull; // Use jakarta for Spring Boot 3+

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignDoctorRequest {
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
}