package com.javaProjects.hospital_management.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;

    private String fullName;

    @OneToOne
    @JoinColumn(name = "speciality_id", nullable = false)
    private Speciality specialization;

    private String phoneNumber;

    private double consultationFee;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
