package com.javaProjects.hospital_management.controller;

import com.javaProjects.hospital_management.dto.response.VideoRoomResponse;
import com.javaProjects.hospital_management.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/create-room")
    public ResponseEntity<?> createRoom(@RequestBody Map<String, Object> request) {
        Long appointmentId = Long.parseLong(request.get("appointmentId").toString());
        return ResponseEntity.ok(videoService.createRoom(appointmentId));
    }

    @GetMapping("/room/{appointmentId}")
    public ResponseEntity<?> getRoomForAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(videoService.getRoomForAppointment(appointmentId));
    }
}