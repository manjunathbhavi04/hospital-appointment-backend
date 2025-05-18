package com.javaProjects.hospital_management.service;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.javaProjects.hospital_management.model.Billing;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Service
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font HEADING_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(79, 129, 189));
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);

    // Format currency values consistently
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public ByteArrayInputStream generateInvoicePdf(Billing billing) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Add metadata
            document.addTitle("Hospital Invoice");
            document.addSubject("Invoice for medical services");
            document.addKeywords("Hospital, Invoice, Medical");
            document.addAuthor("Hospital Management System");
            document.addCreator("Hospital Invoice Service");

            // Add content
            addHeaderAndLogo(document);
            addInvoiceDetails(document, billing);
            addBillingTable(document, billing);
            addFooter(document, billing);

            // Add page numbers and timestamp
            HeaderFooter footer = new HeaderFooter();
            writer.setPageEvent(footer);

            document.close();
        } catch (DocumentException e) {
            logger.error("Error generating invoice PDF for billing ID {}: {}", billing.getBillingId(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate invoice PDF for billing ID " + billing.getBillingId(), e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addHeaderAndLogo(Document document) throws DocumentException {
        // Create a 3-column table for the header
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);

        try {
            headerTable.setWidths(new float[]{1, 2, 1});
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to set column widths in header", e);
        }

        // Column 1: Logo placeholder (you can replace with actual logo image)
        PdfPCell logoCell = new PdfPCell();
        Paragraph logoParagraph = new Paragraph("HMS", TITLE_FONT);
        logoParagraph.setAlignment(Element.ALIGN_CENTER);
        logoCell.addElement(logoParagraph);
        logoCell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(logoCell);

        // Column 2: Hospital name and information
        PdfPCell hospitalInfo = getPdfPCell();
        headerTable.addCell(hospitalInfo);

        // Column 3: Invoice title
        PdfPCell invoiceTitle = new PdfPCell();
        Paragraph title = new Paragraph("INVOICE", SUBTITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        invoiceTitle.addElement(title);
        invoiceTitle.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(invoiceTitle);

        document.add(headerTable);
        document.add(Chunk.NEWLINE);

        // Separator line
        LineSeparator line = new LineSeparator(1, 100, new BaseColor(79, 129, 189), Element.ALIGN_CENTER, -2);
        document.add(line);
        document.add(Chunk.NEWLINE);
    }

    private static PdfPCell getPdfPCell() {
        PdfPCell hospitalInfo = new PdfPCell();
        Paragraph hospitalName = new Paragraph("CITY HOSPITAL", TITLE_FONT);
        hospitalName.setAlignment(Element.ALIGN_CENTER);
        hospitalInfo.addElement(hospitalName);

        Paragraph address = new Paragraph("123 Healthcare Street, Medical District", SMALL_FONT);
        address.setAlignment(Element.ALIGN_CENTER);
        hospitalInfo.addElement(address);

        Paragraph contactInfo = new Paragraph("Phone: +91-123-456-7890 | Email: info@cityhospital.com", SMALL_FONT);
        contactInfo.setAlignment(Element.ALIGN_CENTER);
        hospitalInfo.addElement(contactInfo);

        hospitalInfo.setBorder(Rectangle.NO_BORDER);
        return hospitalInfo;
    }

    private void addInvoiceDetails(Document document, Billing billing) throws DocumentException {
        // Create a 2-column table for the invoice details
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);

        // Patient and doctor information
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);

        Paragraph patientTitle = new Paragraph("BILLED TO:", HEADING_FONT);
        leftCell.addElement(patientTitle);

        Paragraph patientName = new Paragraph("Patient: " + billing.getPatient().getName(), BOLD_FONT);
        leftCell.addElement(patientName);

        // Add patient details if available
        if (billing.getPatient().getPhone() != null) {
            Paragraph patientPhone = new Paragraph("Phone: " + billing.getPatient().getPhone(), NORMAL_FONT);
            leftCell.addElement(patientPhone);
        }

        if (billing.getPatient().getEmail() != null) {
            Paragraph patientEmail = new Paragraph("Email: " + billing.getPatient().getEmail(), NORMAL_FONT);
            leftCell.addElement(patientEmail);
        }

        detailsTable.addCell(leftCell);

        // Invoice details
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);

        Paragraph invoiceDetails = new Paragraph("INVOICE DETAILS:", HEADING_FONT);
        rightCell.addElement(invoiceDetails);

        Paragraph invoiceNumber = new Paragraph("Invoice No: " + billing.getBillingId(), BOLD_FONT);
        rightCell.addElement(invoiceNumber);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Paragraph invoiceDate;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(billing.getBillingDate()));
            invoiceDate = new Paragraph("Date: " + dateFormat.format(date), NORMAL_FONT);
        } catch (Exception e) {
            invoiceDate = new Paragraph("Date: " + billing.getBillingDate(), NORMAL_FONT);
        }
        rightCell.addElement(invoiceDate);

        Paragraph paymentStatus = new Paragraph("Status: " + billing.getPaymentStatus(), BOLD_FONT);
        paymentStatus.getFont().setColor(BaseColor.RED);
        rightCell.addElement(paymentStatus);

        detailsTable.addCell(rightCell);

        document.add(detailsTable);
        document.add(Chunk.NEWLINE);

        // Doctor information
        Paragraph doctorInfo = new Paragraph("Attending Doctor: " + billing.getDoctor().getFullName() +
                " (" + billing.getDoctor().getSpecialization() + ")", NORMAL_FONT);
        doctorInfo.setAlignment(Element.ALIGN_LEFT);
        document.add(doctorInfo);
        document.add(Chunk.NEWLINE);
    }

    private void addBillingTable(Document document, Billing billing) throws DocumentException {
        // Create table for bill items
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        try {
            table.setWidths(new float[]{1, 5, 2, 2, 2});
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to set column widths in billing table", e);
        }

        // Add table headers
        addTableHeader(table);

        // Add table rows
        // Row 1: Consultation Fee
        addTableRow(table, "1", "Professional Consultation Fee", "1",
                CURRENCY_FORMAT.format(billing.getConsultationFee()),
                CURRENCY_FORMAT.format(billing.getConsultationFee()));

        // Row 2: Lab Tests (if any)
        if (billing.getLabFee() > 0) {
            addTableRow(table, "2", "Laboratory Tests and Diagnostics", "1",
                    CURRENCY_FORMAT.format(billing.getLabFee()),
                    CURRENCY_FORMAT.format(billing.getLabFee()));
        }

        // Row 3: Medicines (if any)
        if (billing.getMedicineFee() > 0) {
            addTableRow(table, "3", "Prescribed Medications", "1",
                    CURRENCY_FORMAT.format(billing.getMedicineFee()),
                    CURRENCY_FORMAT.format(billing.getMedicineFee()));
        }

        // Add empty rows to balance the layout if needed
        if (billing.getLabFee() == 0 && billing.getMedicineFee() == 0) {
            for (int i = 0; i < 2; i++) {
                addEmptyRow(table);
            }
        } else if (billing.getLabFee() == 0 || billing.getMedicineFee() == 0) {
            addEmptyRow(table);
        }

        document.add(table);
        document.add(Chunk.NEWLINE);

        // Add total amount
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(50);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        BaseColor lightBlue = new BaseColor(235, 245, 251);

        // Subtotal
        PdfPCell subtotalLabelCell = new PdfPCell(new Phrase("Subtotal:", BOLD_FONT));
        subtotalLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        subtotalLabelCell.setBackgroundColor(lightBlue);
        subtotalLabelCell.setPadding(5);
        totalTable.addCell(subtotalLabelCell);

        PdfPCell subtotalValueCell = new PdfPCell(new Phrase(CURRENCY_FORMAT.format(billing.getTotalAmount()), BOLD_FONT));
        subtotalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        subtotalValueCell.setBackgroundColor(lightBlue);
        subtotalValueCell.setPadding(5);
        totalTable.addCell(subtotalValueCell);

        // Tax (if needed)
        PdfPCell taxLabelCell = new PdfPCell(new Phrase("Tax (0%):", BOLD_FONT));
        taxLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        taxLabelCell.setBackgroundColor(lightBlue);
        taxLabelCell.setPadding(5);
        totalTable.addCell(taxLabelCell);

        PdfPCell taxValueCell = new PdfPCell(new Phrase(CURRENCY_FORMAT.format(0), BOLD_FONT));
        taxValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        taxValueCell.setBackgroundColor(lightBlue);
        taxValueCell.setPadding(5);
        totalTable.addCell(taxValueCell);

        // Total
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK)));
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        totalLabelCell.setBackgroundColor(new BaseColor(79, 129, 189));
        totalLabelCell.setPadding(5);
        totalTable.addCell(totalLabelCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase(CURRENCY_FORMAT.format(billing.getTotalAmount()),
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK)));
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValueCell.setBackgroundColor(new BaseColor(79, 129, 189));
        totalValueCell.setPadding(5);
        totalTable.addCell(totalValueCell);

        document.add(totalTable);
    }

    private void addTableHeader(PdfPTable table) {
        BaseColor headerColor = new BaseColor(79, 129, 189);
        BaseColor textColor = BaseColor.WHITE;
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, textColor);

        String[] headers = {"#", "Description", "Quantity", "Unit Price", "Total"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(headerColor);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void addTableRow(PdfPTable table, String index, String description, String quantity, String unitPrice, String total) {
        BaseColor evenRowColor = new BaseColor(245, 250, 253);

        // Index cell
        PdfPCell indexCell = new PdfPCell(new Phrase(index, NORMAL_FONT));
        indexCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        indexCell.setBackgroundColor(evenRowColor);
        indexCell.setPadding(5);
        table.addCell(indexCell);

        // Description cell
        PdfPCell descCell = new PdfPCell(new Phrase(description, NORMAL_FONT));
        descCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        descCell.setBackgroundColor(evenRowColor);
        descCell.setPadding(5);
        table.addCell(descCell);

        // Quantity cell
        PdfPCell qtyCell = new PdfPCell(new Phrase(quantity, NORMAL_FONT));
        qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qtyCell.setBackgroundColor(evenRowColor);
        qtyCell.setPadding(5);
        table.addCell(qtyCell);

        // Unit price cell
        PdfPCell priceCell = new PdfPCell(new Phrase(unitPrice, NORMAL_FONT));
        priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        priceCell.setBackgroundColor(evenRowColor);
        priceCell.setPadding(5);
        table.addCell(priceCell);

        // Total cell
        PdfPCell totalCell = new PdfPCell(new Phrase(total, NORMAL_FONT));
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalCell.setBackgroundColor(evenRowColor);
        totalCell.setPadding(5);
        table.addCell(totalCell);
    }

    private void addEmptyRow(PdfPTable table) {
        for (int i = 0; i < 5; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(" ", NORMAL_FONT));
            cell.setBorder(Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT);
            table.addCell(cell);
        }
    }

    private void addFooter(Document document, Billing billing) throws DocumentException {
        document.add(Chunk.NEWLINE);

        // Payment instructions
        PdfPTable paymentTable = new PdfPTable(1);
        paymentTable.setWidthPercentage(100);

        PdfPCell paymentHeaderCell = new PdfPCell(new Phrase("PAYMENT INSTRUCTIONS", HEADING_FONT));
        paymentHeaderCell.setBackgroundColor(new BaseColor(235, 245, 251));
        paymentHeaderCell.setBorder(Rectangle.NO_BORDER);
        paymentHeaderCell.setPadding(5);
        paymentTable.addCell(paymentHeaderCell);

        PdfPCell paymentDetailsCell = new PdfPCell();
        paymentDetailsCell.setBorder(Rectangle.NO_BORDER);
        paymentDetailsCell.setPadding(5);

        Paragraph paymentDetails = new Paragraph();
        paymentDetails.add(new Phrase("1. Payment is due within 30 days.\n", NORMAL_FONT));
        paymentDetails.add(new Phrase("2. Please make payments to 'City Hospital' with the invoice number as the reference.\n", NORMAL_FONT));
        paymentDetails.add(new Phrase("3. Bank Details: HDFC Bank, Account No: XXXX-XXXX-XXXX-1234, IFSC: HDFC0001234\n", NORMAL_FONT));

        paymentDetailsCell.addElement(paymentDetails);
        paymentTable.addCell(paymentDetailsCell);

        document.add(paymentTable);
        document.add(Chunk.NEWLINE);

        // Thank you note
        PdfPTable thankYouTable = new PdfPTable(1);
        thankYouTable.setWidthPercentage(100);

        PdfPCell thankYouCell = new PdfPCell();
        thankYouCell.setBorder(Rectangle.NO_BORDER);
        thankYouCell.setPadding(5);

        Paragraph thankYou = new Paragraph("Thank you for choosing City Hospital for your healthcare needs.", BOLD_FONT);
        thankYou.setAlignment(Element.ALIGN_CENTER);
        thankYouCell.addElement(thankYou);

        Paragraph contact = new Paragraph("For any queries regarding this invoice, please contact our billing department at billing@cityhospital.com or call +91-123-456-7890.", SMALL_FONT);
        contact.setAlignment(Element.ALIGN_CENTER);
        thankYouCell.addElement(contact);

        thankYouTable.addCell(thankYouCell);
        document.add(thankYouTable);
    }

    // Class to add page numbers and timestamp to the footer
    private static class HeaderFooter extends PdfPageEventHelper {
        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Page number
            String pageNumber = "Page " + writer.getPageNumber();
            Phrase pagePhrase = new Phrase(pageNumber, font);

            // Get width and height of the page
            float pageWidth = document.right() - document.left();
            float pageHeight = document.top() - document.bottom();

            // Add page number at the bottom center
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    pagePhrase,
                    document.left() + pageWidth / 2,
                    document.bottom() - 20, 0);

            // Add timestamp at the bottom right
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = "Generated on: " + sdf.format(new Date());
            Phrase timestampPhrase = new Phrase(timestamp, font);

            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    timestampPhrase,
                    document.right(),
                    document.bottom() - 20, 0);
        }
    }
}