package com.javaProjects.hospital_management.service;

import com.javaProjects.hospital_management.dto.response.SpecialityResponse;
import com.javaProjects.hospital_management.exception.ResourceNotFound;
import com.javaProjects.hospital_management.model.Speciality;
import com.javaProjects.hospital_management.repository.SpecialityRepository;
import com.javaProjects.hospital_management.transformer.SpecialityTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialityService {
    private final SpecialityRepository specialityRepository;

    public SpecialityResponse addSpeciality(String name) {

        Speciality spec = specialityRepository.findByName(name);

        if(spec != null) {
            throw new RuntimeException("Speciality with name: " + name + " already exists.");
        }

        Speciality speciality = Speciality.builder()
                .name(name)
                .build();
        return SpecialityTransformer.specialityToSpecialityResponse(specialityRepository.save(speciality));
    }

    public SpecialityResponse getBySpeciality(String specialization) {
        Speciality speciality = specialityRepository.findByName(specialization);
        if(speciality == null) {
            throw new ResourceNotFound("This specialization is not available in our hospital add it");
        }
        return SpecialityTransformer.specialityToSpecialityResponse(speciality);
    }

    public List<SpecialityResponse> getAllSpeciality() {
        return specialityRepository.findAll().stream().map(SpecialityTransformer::specialityToSpecialityResponse).toList();
    }

    public SpecialityResponse getSpeciality(Long id) {
        return SpecialityTransformer.specialityToSpecialityResponse(specialityRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Invalid Speciality ID")));
    }

    public SpecialityResponse updateSpeciality(String name, Long id) {
        Speciality speciality  =specialityRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Invalid Speciality ID"));
        speciality.setName(name);
        specialityRepository.save(speciality);
        return SpecialityTransformer.specialityToSpecialityResponse(speciality);
    }
}
