package com.javaProjects.hospital_management.dto.response;

import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String reason;
    private String problemDescription;
} 