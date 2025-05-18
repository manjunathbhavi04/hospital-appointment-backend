package com.javaProjects.hospital_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoRoomResponse {
    private String url;
    private String name;
    private String expiresAt;
}
