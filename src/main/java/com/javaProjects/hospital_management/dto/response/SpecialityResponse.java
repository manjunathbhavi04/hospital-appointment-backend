package com.javaProjects.hospital_management.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialityResponse {
    private Long id;
    private String name;
}
