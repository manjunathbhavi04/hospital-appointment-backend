package com.javaProjects.hospital_management.dto.request;

import com.javaProjects.hospital_management.Enum.AppointmentMode;
import lombok.*;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Email; // Use jakarta for Spring Boot 3+
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent; // To ensure date is not in the past
import jakarta.validation.constraints.Pattern; // For phone number validation

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotBlank(message = "Patient email is required")
    @Email(message = "Invalid email format")
    private String patientEmail;

    @NotBlank(message = "Patient number is required")
    // @Pattern(regexp = "^[0-9]{10}$", message = "Patient number must be 10 digits")
    @Pattern(regexp = "^[0-9]{10}$", message = "Patient number must be 10 digit")
    private String patientNumber;

    @NotNull(message = "Appointment mode is required")
    private AppointmentMode mode;

    @NotBlank(message = "Problem description is required")
    private String problemDescription;

    @NotNull(message = "Appointment date and time is required")
    @FutureOrPresent(message = "Appointment date and time must be in the present or future")
    private LocalDateTime appointmentDateTime;
}