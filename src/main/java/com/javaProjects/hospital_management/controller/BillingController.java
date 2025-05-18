package com.javaProjects.hospital_management.controller;

import java.io.ByteArrayInputStream;

import com.javaProjects.hospital_management.dto.response.BillResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javaProjects.hospital_management.model.Billing;
import com.javaProjects.hospital_management.service.BillingService;
import com.javaProjects.hospital_management.service.InvoiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billingService;
    private final InvoiceService invoiceService;

    @PostMapping("/generate")
    public ResponseEntity<BillResponse> generateBill(@RequestParam Long appointmentId,
        @RequestParam Long patientId,
        @RequestParam Long doctorId,
        @RequestParam double labFee,
        @RequestParam double medicineFee
    ) {
        BillResponse billing = billingService.generateBill(appointmentId, patientId, doctorId, labFee, medicineFee);
        return new ResponseEntity<>(billing, HttpStatus.CREATED);
    }

    @PutMapping("/{billingId}/pay")
    public ResponseEntity<Billing> markAsPaid(@PathVariable Long billingId) {
        return new ResponseEntity<>(billingService.markAsPaid(billingId), HttpStatus.OK);
    }

    @GetMapping("/{billingId}/download-invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long billingId) throws Exception {

        Billing billing = billingService.getBillingById(billingId);

        ByteArrayInputStream bis = invoiceService.generateInvoicePdf(billing);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; fullName=invoice_" + billingId
                 + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());

    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<BillResponse> getBillByAppointment(@PathVariable("appointmentId") Long id) {
        return new ResponseEntity<>(billingService.getBillByAppointment(id), HttpStatus.OK);
    }
}
