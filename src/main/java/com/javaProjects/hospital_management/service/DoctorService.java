package com.javaProjects.hospital_management.service;

import com.javaProjects.hospital_management.Enum.AppointmentStatus;
import com.javaProjects.hospital_management.dto.request.DoctorUpdate;
import com.javaProjects.hospital_management.dto.response.AppointmentResponse;
import com.javaProjects.hospital_management.dto.response.DoctorResponse;
import com.javaProjects.hospital_management.exception.ResourceNotFound;
import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.model.Doctor;
import com.javaProjects.hospital_management.model.Speciality;
import com.javaProjects.hospital_management.repository.AppointmentRepository;
import com.javaProjects.hospital_management.repository.DoctorRepository;
import com.javaProjects.hospital_management.transformer.AppointmentTransformer;
import com.javaProjects.hospital_management.transformer.DoctorTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final SpecialityService specialityService;

    public List<DoctorResponse> getAllDoctorResponses() {
        return doctorRepository.findAll().stream().map(DoctorTransformer::doctorToDoctorResponse).toList();
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<DoctorResponse> getAllDoctors1() {
        return doctorRepository.findAll().stream().map(DoctorTransformer::doctorToDoctorResponse).toList();
    }

    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId).orElseThrow(
                () -> new ResourceNotFound("No Doctor with Id: "+ doctorId)
        );
    }

    public List<AppointmentResponse> getScheduledAppointments(Long doctorId) {
        return appointmentRepository.findByDoctor_DoctorIdAndStatus(doctorId, AppointmentStatus.SCHEDULED)
                .stream()
                .map(AppointmentTransformer::appointmentToAppointmentResponse)
                .collect(Collectors.toList());
    }

    public DoctorResponse updateDoctor(Long id, DoctorUpdate doctor) {
        Doctor doc = doctorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Invalid Doctor ID")
        );
        Speciality speciality = specialityService.getSpecialityByName(doctor.getSpecialization().getName());
        doc.setConsultationFee(doctor.getConsultationFee());
        doc.setFullName(doctor.getFullName());
        doc.setSpecialization(speciality);
        doc.setPhoneNumber(doctor.getPhoneNumber());

        doctorRepository.save(doc);

        return DoctorTransformer.doctorToDoctorResponse(doc);
    }
}
