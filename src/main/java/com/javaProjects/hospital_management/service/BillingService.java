package com.javaProjects.hospital_management.service;

import com.javaProjects.hospital_management.Enum.PaymentStatus;
import com.javaProjects.hospital_management.dto.response.BillResponse;
import com.javaProjects.hospital_management.exception.ResourceNotFound;
import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.model.Billing;
import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.model.Patient;
import com.javaProjects.hospital_management.repository.AppointmentRepository;
import com.javaProjects.hospital_management.repository.BillingRepository;
import com.javaProjects.hospital_management.repository.DoctorRepository;
import com.javaProjects.hospital_management.repository.PatientRepository;
import com.javaProjects.hospital_management.transformer.BillingTransformer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillingRepository billingRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public BillResponse generateBill(Long appointmentId, Long patientId, Long doctorId, double labFee, double medicineFee) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        double totalAmount = 0;
        if(appointment != null) {
            Optional<Billing> billing = billingRepository.findByAppointment(appointment);

            if(billing.isPresent()) {
                Billing bill = billing.get();

                totalAmount = bill.getDoctor().getConsultationFee() + medicineFee + labFee;

                bill.setBillingDate(LocalDate.now());
                bill.setPaymentStatus(PaymentStatus.PAID);
                bill.setConsultationFee(bill.getDoctor().getConsultationFee());
                bill.setLabFee(labFee);
                bill.setMedicineFee(medicineFee);
                bill.setTotalAmount(totalAmount);

                return BillingTransformer.billingToBillingResponse(billingRepository.save(bill));
            }
        }


        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new ResourceNotFound("invalid patient id")
        );

        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new ResourceNotFound("invalid doctor id")
        );

        if(billingRepository.existsByDoctor(doctor) && billingRepository.existsByPatient(patient)) {
            throw new RuntimeException("Bill already generated");
        }

        Billing billing = Billing.builder()
                .patient(patient)
                .doctor(doctor)
                .consultationFee(doctor.getConsultationFee())
                .labFee(labFee)
                .medicineFee(medicineFee)
                .totalAmount(totalAmount)
                .build();

        billing.setBillingDate(LocalDate.now());

        billing.setPaymentStatus(PaymentStatus.UNPAID);

        return BillingTransformer.billingToBillingResponse(billingRepository.save(billing));
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

    public BillResponse getBillByAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Thsi appointment id is not assigned to any Bill"));
        return BillingTransformer.billingToBillingResponse(billingRepository.findByAppointment(appointment).orElseThrow(() -> new RuntimeException("Invalid Appointment")));
    }
}
