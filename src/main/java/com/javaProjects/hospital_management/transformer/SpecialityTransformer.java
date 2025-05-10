package com.javaProjects.hospital_management.transformer;

import com.javaProjects.hospital_management.dto.response.SpecialityResponse;
import com.javaProjects.hospital_management.model.Speciality;

public class SpecialityTransformer {
    public static SpecialityResponse specialityToSpecialityResponse(Speciality speciality) {
        return SpecialityResponse.builder()
                .id(speciality.getId()) // Map the ID of the speciality
                .name(speciality.getName())
                .build();
    }
}