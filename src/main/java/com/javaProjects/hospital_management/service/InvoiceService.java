package com.javaProjects.hospital_management.service;

import com.javaProjects.hospital_management.model.Billing;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.slf4j.Logger; // Import for logging
import org.slf4j.LoggerFactory; // Import for logging

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException; // Import for IOException

@Service
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class); // Initialize logger

    public ByteArrayInputStream generateInvoicePdf(Billing billing) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Hospital Invoice", font);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Billing ID: " + billing.getBillingId()));
            document.add(new Paragraph("Patient: " + billing.getPatient().getName()));
            document.add(new Paragraph("Doctor: " + billing.getDoctor().getFullName()));
            document.add(new Paragraph("Date: " + billing.getBillingDate()));
            document.add(new Paragraph("Consultation Fee: ₹" + billing.getConsultationFee()));
            document.add(new Paragraph("Lab Fee: ₹" + billing.getLabFee()));
            document.add(new Paragraph("Medicine Fee: ₹" + billing.getMedicineFee()));
            document.add(new Paragraph("Total: ₹" + billing.getTotalAmount()));
            document.add(new Paragraph("Payment Status: " + billing.getPaymentStatus()));

            document.close();
        } catch (DocumentException e) { // Catch specific iText exceptions
            logger.error("Error generating invoice PDF for billing ID {}: {}", billing.getBillingId(), e.getMessage(), e);
            // Re-throw a more specific runtime exception to be handled by a global exception handler
            throw new RuntimeException("Failed to generate invoice PDF for billing ID " + billing.getBillingId(), e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}