package com.javaProjects.hospital_management.repository;

import com.javaProjects.hospital_management.model.VideoRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRoomRepository extends JpaRepository<VideoRoom, Long> {
    Optional<VideoRoom> findByAppointmentId(Long appointmentId);
}
