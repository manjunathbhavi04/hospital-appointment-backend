package com.javaProjects.hospital_management.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String department;
    private String phone;
}
