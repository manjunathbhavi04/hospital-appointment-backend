package com.javaProjects.hospital_management.model;

import com.javaProjects.hospital_management.Enum.AppointmentMode;
import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.Banner;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private AppointmentMode mode;

    private String problemDescription;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(optional = true)
    @JoinColumn(name = "doctor_id", nullable = true)
    private Doctor doctor;
}


