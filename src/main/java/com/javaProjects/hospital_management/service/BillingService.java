package com.javaProjects.hospital_management.service;

import com.javaProjects.hospital_management.Enum.PaymentStatus;
import com.javaProjects.hospital_management.exception.ResourceNotFound;
import com.javaProjects.hospital_management.model.Billing;
import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.model.Patient;
import com.javaProjects.hospital_management.repository.BillingRepository;
import com.javaProjects.hospital_management.repository.DoctorRepository;
import com.javaProjects.hospital_management.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillingRepository billingRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public Billing generateBill(Long patientId, Long doctorId, double labFee, double medicineFee) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new ResourceNotFound("invalid patient id")
        );

        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new ResourceNotFound("invalid doctor id")
        );

        Billing billing = Billing.builder()
                .patient(patient)
                .doctor(doctor)
                .consultationFee(doctor.getConsultationFee())
                .labFee(labFee)
                .medicineFee(medicineFee)
                .build();

        double totalAmount = doctor.getConsultationFee() + medicineFee + labFee;

        billing.setTotalAmount(totalAmount);

        billing.setBillingDate(LocalDate.now());

        billing.setPaymentStatus(PaymentStatus.UNPAID);

        return billingRepository.save(billing);
    }

    public Billing markAsPaid(Long billingId) {
        Billing billing = billingRepository.findById(billingId).orElseThrow(
                () -> new ResourceNotFound("Invalid billing id")
        );

        billing.setPaymentStatus(PaymentStatus.PAID);

        return billingRepository.save(billing);
    }

    public Billing getBillingById(Long billingId) {
        return billingRepository.findById(billingId).orElseThrow(() -> new ResourceNotFound("Invalid Billing Id"));
    }
}
