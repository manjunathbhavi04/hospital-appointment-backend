package com.javaProjects.hospital_management.transformer;

import com.javaProjects.hospital_management.model.Billing;
import com.javaProjects.hospital_management.dto.response.BillResponse;


public class BillingTransformer {
    public static BillResponse billingToBillingResponse(Billing billing) {
        return BillResponse.builder()
                .billingId(billing.getBillingId())
                .patientName(billing.getPatient().getName())
                .doctorName(billing.getDoctor().getFullName())
                .billingDate(billing.getBillingDate())
                .consultationFee(billing.getConsultationFee())
                .paymentStatus(billing.getPaymentStatus())
                .totalAmount(billing.getTotalAmount())
                .medicineFee(billing.getMedicineFee())
                .labFee(billing.getLabFee())
                .build();
    }
}
