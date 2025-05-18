package com.javaProjects.hospital_management.service;

import com.javaProjects.hospital_management.dto.response.VideoRoomResponse;
import com.javaProjects.hospital_management.exception.ResourceNotFound;
import com.javaProjects.hospital_management.model.Appointment;
import com.javaProjects.hospital_management.model.VideoRoom;
import com.javaProjects.hospital_management.repository.AppointmentRepository;
import com.javaProjects.hospital_management.repository.VideoRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final AppointmentRepository appointmentRepository;
    private final VideoRoomRepository videoRoomRepository;
    private final EmailService emailService;

    @Value("${daily.api.key}")
    private String dailyApiKey;

    private final String DAILY_API_URL = "https://api.daily.co/v1";

    public VideoRoomResponse createRoom(Long appointmentId) {
        // Fetch the appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFound("No appointment found with ID: " + appointmentId));

        // Check if room already exists
        Optional<VideoRoom> existingRoom = videoRoomRepository.findByAppointmentId(appointmentId);
        if (existingRoom.isPresent()) {
            VideoRoom room = existingRoom.get();
            return new VideoRoomResponse(room.getRoomUrl(), room.getRoomName(), room.getExpiresAt().toString());
        }

        // Create a unique room name
        String roomName = "appt-" + appointmentId + "-" + UUID.randomUUID().toString().substring(0, 8);

        // Set up API call to Daily.co
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(dailyApiKey);

        // Set up room configuration
        Map<String, Object> roomConfig = new HashMap<>();
        roomConfig.put("name", roomName);
        roomConfig.put("privacy", "public");

        // Add room expiration (e.g., 24 hours from now)
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(24);

        // Make API call to create room
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(roomConfig, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                DAILY_API_URL + "/rooms",
                HttpMethod.POST,
                request,
                Map.class
        );

        // Process the response
        Map<String, Object> responseBody = response.getBody();

        if (responseBody != null) {
            String roomUrl = "https://bookyouhealth.daily.co/" + roomName;
            if (responseBody.get("url") != null) {
                roomUrl = responseBody.get("url").toString();
            }

            // Save room info in database
            VideoRoom videoRoom = new VideoRoom();
            videoRoom.setAppointment(appointment);
            videoRoom.setRoomName(roomName);
            videoRoom.setRoomUrl(roomUrl);
            videoRoom.setCreatedAt(LocalDateTime.now());
            videoRoom.setExpiresAt(expiryTime);
            videoRoomRepository.save(videoRoom);

            // Send email to patient with the room link
            System.out.println("Video mail below...");
            if (appointment.getPatient() != null && appointment.getPatient().getEmail() != null) {
                emailService.sendEmail(
                        appointment.getPatient().getEmail(),
                        "Video Consultation Link for Your Appointment",
                        "Dear " + appointment.getPatient().getName() + ",\n\n" +
                                "Your video consultation is scheduled for " + appointment.getScheduledTime() + ".\n" +
                                "Please use this link to join the consultation: " + roomUrl + "\n\n" +
                                "Best regards,\nHospital Management Team"
                );
            }

            return new VideoRoomResponse(roomUrl, roomName, expiryTime.toString());
        } else {
            throw new RuntimeException("Failed to create video room with Daily.co API");
        }
    }

    public VideoRoomResponse getRoomForAppointment(Long appointmentId) {
        Optional<VideoRoom> videoRoom = videoRoomRepository.findByAppointmentId(appointmentId);

        if (videoRoom.isPresent()) {
            VideoRoom room = videoRoom.get();
            return new VideoRoomResponse(room.getRoomUrl(), room.getRoomName(), room.getExpiresAt().toString());
        } else {
            // If no room exists, create one
            return createRoom(appointmentId);
        }
    }
}