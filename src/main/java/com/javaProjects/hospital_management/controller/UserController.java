package com.javaProjects.hospital_management.controller;

import com.javaProjects.hospital_management.model.User;
import com.javaProjects.hospital_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/{username}")
    public User getByUsername(@PathVariable("username") String username) {
        System.out.println("getting user");
        return userService.getByUserName(username);
    }
}
