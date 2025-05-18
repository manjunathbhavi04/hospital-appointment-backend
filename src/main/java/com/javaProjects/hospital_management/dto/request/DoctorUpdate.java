package com.javaProjects.hospital_management.dto.request;

import com.javaProjects.hospital_management.model.Speciality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdate {
    private String fullName;
    private String phoneNumber;
    private Speciality specialization;
    private double consultationFee;
}
