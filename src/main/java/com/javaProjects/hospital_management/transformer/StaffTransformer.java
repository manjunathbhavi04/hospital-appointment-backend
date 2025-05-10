package com.javaProjects.hospital_management.transformer;


import com.javaProjects.hospital_management.dto.request.StaffRequest;
import com.javaProjects.hospital_management.dto.response.StaffResponse;
import com.javaProjects.hospital_management.model.Staff;
import com.javaProjects.hospital_management.model.User;

public class StaffTransformer {
    public static Staff staffRequestToStaff(StaffRequest staffRequest, User user) {
        return Staff.builder()
                .fullName(staffRequest.getFullName())
                .department(staffRequest.getDepartment())
                .phoneNumber(staffRequest.getPhone())
                .user(user)
                .build();
    }

    public static StaffResponse staffToStaffResponse(Staff staff) {
        return StaffResponse.builder()
                .id(staff.getStaffId()) // Added staffId for completeness
                .fullName(staff.getFullName())
                .department(staff.getDepartment())
                .phoneNumber(staff.getPhoneNumber())
                // Added email from associated User for completeness, with null check
                .email(staff.getUser() != null ? staff.getUser().getEmail() : null)
                .build();
    }
}