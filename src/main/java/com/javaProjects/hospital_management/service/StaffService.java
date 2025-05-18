package com.javaProjects.hospital_management.service;

import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import com.javaProjects.hospital_management.Enum.PaymentStatus;
import com.javaProjects.hospital_management.dto.response.AppointmentResponse; // Added for consistency
import com.javaProjects.hospital_management.dto.response.DoctorResponse; // No longer directly used for return type, but kept if needed for internal ops
import com.javaProjects.hospital_management.dto.response.StaffResponse;
import com.javaProjects.hospital_management.exception.ResourceNotFound;
import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.model.Billing;
import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.model.Speciality; // Kept as Speciality is part of Doctor
import com.javaProjects.hospital_management.repository.AppointmentRepository;
import com.javaProjects.hospital_management.repository.BillingRepository;
import com.javaProjects.hospital_management.repository.StaffRepository;
import com.javaProjects.hospital_management.transformer.AppointmentTransformer; // Added for consistency
import com.javaProjects.hospital_management.transformer.StaffTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors; // Added for stream operations

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService; // Injecting DoctorService is fine
    private final EmailService emailService;
    private final BillingRepository billingRepository;
    // private final SpecialityService specialityService; // Removed - if not used, remove it

    public List<StaffResponse> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(StaffTransformer::staffToStaffResponse)
                .collect(Collectors.toList()); // Use collect for clarity
    }

    // Option 1: Automatic Doctor Assignment (Needs more robust logic for production)
    public AppointmentResponse assignDoctorAutomatically(Long appointmentId) { // Renamed for clarity, returning DTO
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFound("No appointment found with ID: " + appointmentId));

        List<Doctor> availableDoctors = doctorService.getAllDoctors(); // Returns entities, which is fine for internal logic

        Doctor assignedDoctor = null;
        for (Doctor doctor : availableDoctors) {
            // Consider more robust matching here (e.g., exact speciality name, or more complex symptom matching)
            // This example remains simple for now.
            if (appointment.getProblemDescription() != null && doctor.getSpecialization() != null &&
                    doctor.getSpecialization().getName().toLowerCase().contains(appointment.getProblemDescription().toLowerCase()) ||
                    appointment.getProblemDescription().toLowerCase().contains(doctor.getSpecialization().getName().toLowerCase()))
            {
                assignedDoctor = doctor;
                break; // Assign the first matching doctor
            }
            // If you want to match keywords more broadly, you might need to refine this logic
            // Example: if a doctor's specialization has a keyword, check if description contains that keyword
            // Make sure doctor.getSpecialization() and doctor.getSpecialization().getKeyword() are not null
            if (doctor.getSpecialization() != null && doctor.getSpecialization().getKeyword() != null &&
                    appointment.getProblemDescription().toLowerCase().contains(doctor.getSpecialization().getKeyword().toLowerCase())) {
                assignedDoctor = doctor;
                break;
            }
        }

        if (assignedDoctor == null) {
            throw new ResourceNotFound("No Doctor with a suitable specialization found for the problem description.");
        }

        appointment.setDoctor(assignedDoctor);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment); // Save the changes

        // Optionally, notify the patient/doctor here about the automatic assignment
         emailService.sendEmail(savedAppointment.getPatient().getEmail(),
                 "Doctor Assigned to Your Appointment",
                 "Dear " + savedAppointment.getPatient().getName() + ",\n" +
                 "A doctor (" + assignedDoctor.getFullName() + ") has been assigned to your appointment. You will be notified of the scheduled time soon."
         );

        return AppointmentTransformer.appointmentToAppointmentResponse(savedAppointment);
    }

    // Option 2: Manual Doctor Assignment (Likely redundant if AppointmentService.assignDoctor exists)
    // Renamed from assignDoctor1 for clarity.
    public AppointmentResponse assignDoctorManually(Long appointmentId, Long doctorId) { // Returning DTO
        // Consistency: Use ResourceNotFound
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFound("Appointment not found with ID: " + appointmentId));

        Billing billing = billingRepository.findByAppointment(appointment).orElseThrow(
                () -> new ResourceNotFound("Invalid Appointment id for bill")
        );

        // Use doctorService to get the doctor, ensuring consistency
        Doctor doctor = doctorService.getDoctorById(doctorId);

        billing.setDoctor(doctor);
        billing.setPaymentStatus(PaymentStatus.UNPAID);
        billingRepository.save(billing);

        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        // Check if user and email exist before sending email
        if (doctor.getUser() != null && doctor.getUser().getEmail() != null) {
            emailService.sendEmail(doctor.getUser().getEmail(),
                    "New Appointment Assigned",
                    "Dear " + doctor.getFullName() + ",\n" + "\nYou have been assigned a new patient appointment. Please check your schedule."
            );
        } else {
            // Log if email cannot be sent due to missing user/email
            System.err.println("Cannot send email to doctor " + doctor.getFullName() + ": User or email not found.");
        }

        Appointment savedAppointment = appointmentRepository.save(appointment); // Save the changes

        // Optionally, notify the patient/doctor here about the automatic assignment
        emailService.sendEmail(savedAppointment.getPatient().getEmail(),
                "Doctor Assigned to Your Appointment",
                "Dear " + savedAppointment.getPatient().getName() + ",\n" +
                        "A doctor (" + doctor.getFullName() + ") has been assigned to your appointment. You will be notified of the scheduled time soon."
        );

        return AppointmentTransformer.appointmentToAppointmentResponse(savedAppointment);
    }

    // CRITICAL FIX: Add save() call here. Also, consider returning DTO for consistency.
    public AppointmentResponse updateAppointmentStatus(Long appointmentId) { // Renamed from updateStatus, returning DTO
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFound("Invalid appointment ID: " + appointmentId));

        appointment.setStatus(AppointmentStatus.COMPLETED);

        Appointment updatedAppointment = appointmentRepository.save(appointment); // FIX: Save the updated appointment

        // Optionally, notify the patient that the appointment is completed
        // if (updatedAppointment.getPatient() != null && updatedAppointment.getPatient().getEmail() != null) {
        //     emailService.sendEmail(updatedAppointment.getPatient().getEmail(),
        //             "Appointment Completed",
        //             "Dear " + updatedAppointment.getPatient().getName() + ",\n" +
        //             "Your appointment has been marked as completed. Thank you for choosing our hospital."
        //     );
        // }

        return AppointmentTransformer.appointmentToAppointmentResponse(updatedAppointment);
    }
}