package com.javaProjects.hospital_management.service;

import com.javaProjects.hospital_management.exception.ResourceNotFound; // Keep if used elsewhere
import com.javaProjects.hospital_management.model.Patient;
import com.javaProjects.hospital_management.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional; // Ensure this import is present

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public Patient addPatient(Patient patient) {
        // Check if a patient with this email already exists
        Optional<Patient> existingPatient = patientRepository.findByEmail(patient.getEmail());

        if (existingPatient.isPresent()) {
            // If a patient with this email exists, throw an exception
            // It's good practice to have a specific exception for this scenario
            // e.g., throw new PatientAlreadyExistsException("Patient with email: " + patient.getEmail() + " already exists.");
            // For now, using RuntimeException as a placeholder; consider creating a custom exception.
            throw new RuntimeException("Patient with email: " + patient.getEmail() + " already exists. Cannot add duplicate.");
        }

        // Step 3: If no patient with this email exists, save the new patient
        return patientRepository.save(patient);
    }

    // You likely also need a method to retrieve a patient by ID, which is a common operation:
    public Patient getPatientById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFound("Patient not found with ID: " + patientId));
    }
}