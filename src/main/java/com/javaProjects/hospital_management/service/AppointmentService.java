package com.javaProjects.hospital_management.service;

import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import com.javaProjects.hospital_management.dto.request.AppointmentRequest;
import com.javaProjects.hospital_management.dto.response.AppointmentResponse;
import com.javaProjects.hospital_management.exception.ResourceNotFound;
import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.model.Patient;
import com.javaProjects.hospital_management.model.User;
import com.javaProjects.hospital_management.repository.AppointmentRepository;
import com.javaProjects.hospital_management.repository.DoctorRepository;
import com.javaProjects.hospital_management.repository.PatientRepository;
import com.javaProjects.hospital_management.repository.UserRepository;
import com.javaProjects.hospital_management.transformer.AppointmentTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    // Initial appointment booking by patient
    public Appointment bookAppointment(AppointmentRequest appointmentRequest) {
        // Check if patient already exists by email. If so, use the existing patient.
        // If not, create and save a new patient.
        Patient patient = patientRepository.findByEmail(appointmentRequest.getPatientEmail())
                .orElseGet(() -> {
                    Patient newPatient = Patient.builder()
                            .name(appointmentRequest.getPatientName())
                            .email(appointmentRequest.getPatientEmail())
                            .phone(appointmentRequest.getPatientNumber())
                            .build();
                    return patientRepository.save(newPatient);
                });

        // Create the appointment without doctor assignment
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .status(AppointmentStatus.PENDING)
                .problemDescription(appointmentRequest.getProblemDescription())
                .mode(appointmentRequest.getMode())
                .build();

        // Send mail to the patient when they book an appointment
        emailService.sendEmail(patient.getEmail(),
                "Appointment Booked",
                "Dear " + patient.getName() + "," + "\nYour Appointment has been booked. We will get back to you when a doctor is assigned."
        );

        return appointmentRepository.save(appointment);
    }

    // Staff assigns doctor to appointment
    public Appointment assignDoctor(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFound("Appointment not found with ID: " + appointmentId));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFound("Doctor not found with ID: " + doctorId));

        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        // Notify patient about doctor assignment
        emailService.sendEmail(appointment.getPatient().getEmail(),
                "Doctor Assigned to Your Appointment",
                "Dear " + appointment.getPatient().getName() + ",\n" +
                        "A doctor has been assigned to your appointment. You will be notified of the scheduled time soon."
        );

        return appointmentRepository.save(appointment);
    }

    // Get all appointments by their status (e.g., PENDING, SCHEDULED, COMPLETED)
    public List<AppointmentResponse> getAllAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status)
                .stream()
                .map(AppointmentTransformer::appointmentToAppointmentResponse)
                .collect(Collectors.toList());
    }

    // Get all appointments for the currently logged-in doctor
    public List<AppointmentResponse> getDoctorAppointments() {
        return appointmentRepository.findByDoctor_DoctorId(getCurrentDoctorId())
                .stream()
                .map(AppointmentTransformer::appointmentToAppointmentResponse)
                .collect(Collectors.toList());
    }

    // Get scheduled appointments for the currently logged-in doctor
    public List<AppointmentResponse> getDoctorScheduledAppointments() {
        return appointmentRepository.findByDoctor_DoctorIdAndStatus(getCurrentDoctorId(), AppointmentStatus.SCHEDULED)
                .stream()
                .map(AppointmentTransformer::appointmentToAppointmentResponse)
                .collect(Collectors.toList());
    }

    // Update the status of an appointment (can only be done by the assigned doctor)
    public AppointmentResponse updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFound("Appointment not found with ID: " + appointmentId));

        // Security check: Ensure only the assigned doctor can update this appointment
        if (appointment.getDoctor() == null || !appointment.getDoctor().getDoctorId().equals(getCurrentDoctorId())) {
            throw new ResourceNotFound("Not authorized to update this appointment. Doctor not assigned or you are not the assigned doctor.");
        }

        appointment.setStatus(status);
        return AppointmentTransformer.appointmentToAppointmentResponse(appointmentRepository.save(appointment));
    }

    // Helper method to get the Doctor ID of the currently authenticated user
    private Long getCurrentDoctorId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFound("User not found with username: " + username));

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFound("Doctor profile not found for user: " + username));
        return doctor.getDoctorId();
    }
}