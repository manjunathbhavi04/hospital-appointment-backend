package com.javaProjects.hospital_management.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private Long doctorId;

    private String fullName;

    private String specialization;

    private String phoneNumber;

    private String email;
}
