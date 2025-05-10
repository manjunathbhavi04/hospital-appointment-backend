package com.javaProjects.hospital_management;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.javaProjects.hospital_management.Enum.Role;
import com.javaProjects.hospital_management.model.User;
import com.javaProjects.hospital_management.repository.UserRepository;

@SpringBootApplication
@EnableAsync
public class HospitalManagementApplication {

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {

		SpringApplication.run(HospitalManagementApplication.class, args);

	}

	@Bean
	public CommandLineRunner runner(UserRepository userRepository) {
		return args -> {
			if (userRepository.count() == 0) {
				userRepository.saveAll(List.of(
						User.builder().username("admin").password(passwordEncoder.encode("admin123")).email("admin@gmail.com").role(Role.ADMIN).build()
						// User.builder().username("staff").password(passwordEncoder.encode("staff123")).email("staff@gmail.com").role(Role.STAFF).build(),
						// User.builder().username("doctor").password(passwordEncoder.encode("doctor123")).email("doctor@gmail.com").role(Role.DOCTOR).build()
				));
			}
		};
	}

}
