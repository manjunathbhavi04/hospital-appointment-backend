package com.javaProjects.hospital_management.model;

import com.javaProjects.hospital_management.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billingId;

    @OneToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    private LocalDate billingDate;

    private double consultationFee;
    private double labFee;
    private double medicineFee;

    private double totalAmount;

    @OneToOne
    @JoinColumn(name="appointment_id")
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

}
