package com.javaProjects.hospital_management.service;

import com.javaProjects.hospital_management.exception.ResourceNotFound;
import com.javaProjects.hospital_management.model.User;
import com.javaProjects.hospital_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getByUserName(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFound("No User with username: "+ username));
    }

}
