package com.javaProjects.hospital_management.transformer;

import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import com.javaProjects.hospital_management.dto.request.AppointmentRequest;
import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.model.Patient;

import java.time.LocalDateTime;

import com.javaProjects.hospital_management.dto.response.AppointmentResponse;

public class AppointmentTransformer {
    public static Appointment appointmentRequestToAppointment(AppointmentRequest appointmentRequest, Patient patient) {
        return Appointment.builder()
                .patient(patient)
                .problemDescription(appointmentRequest.getProblemDescription())
                .mode(appointmentRequest.getMode())
                .status(AppointmentStatus.PENDING)
                .scheduledTime(LocalDateTime.now().plusDays(1))
                .build();
    }
    public static AppointmentResponse appointmentToAppointmentResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor() != null ? appointment.getDoctor().getDoctorId() : null)
                .doctorName(appointment.getDoctor() != null ? appointment.getDoctor().getFullName() : "Not Assigned")
                .patientId(appointment.getPatient().getPatientId())
                .patientName(appointment.getPatient().getName())
                .problemDescription(appointment.getProblemDescription())
                .status(appointment.getStatus())
                .appointmentDateTime(appointment.getScheduledTime())
                .mode(appointment.getMode())
                .build();
    }
}
