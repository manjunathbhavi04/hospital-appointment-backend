package com.javaProjects.hospital_management.dto.response;

import com.javaProjects.hospital_management.Enum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponse {
    private Long billingId;
    private String patientName;
    private String doctorName;
    private LocalDate billingDate;
    private double consultationFee;
    private double labFee;
    private double medicineFee;

    private double totalAmount;

    private PaymentStatus paymentStatus;
}
