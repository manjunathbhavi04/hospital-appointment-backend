package com.javaProjects.hospital_management.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {
    private Long id;

    private String fullName;

    private String department;

    private String phoneNumber;

    private String email;
}
