package com.javaProjects.hospital_management.controller;

import com.javaProjects.hospital_management.dto.response.SpecialityResponse;
import com.javaProjects.hospital_management.model.Speciality;
import com.javaProjects.hospital_management.service.SpecialityService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/speciality")
public class SpecialityController {

    private final SpecialityService specialityService;

    @GetMapping
    public ResponseEntity<List<SpecialityResponse>> getAllSpeciality() {
        return new ResponseEntity<>(specialityService.getAllSpeciality(), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Speciality> getSpecialityByName(@PathVariable("name") String name) {
        return new ResponseEntity<>(specialityService.getSpecialityByName(name), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialityResponse> getSpeciality(@PathVariable("id") Long id) {
        return new ResponseEntity<>(specialityService.getSpeciality(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<SpecialityResponse> updateSpeciality(@RequestParam("name") String name, @PathVariable("id") Long id) {
        return new ResponseEntity<>(specialityService.updateSpeciality(name, id), HttpStatus.OK);
    }

    @PostMapping("/add/{name}")
    public ResponseEntity<SpecialityResponse> addSpeciality(@PathVariable("name") String name) {
        SpecialityResponse specialityResponse = specialityService.addSpeciality(name);
        if(specialityResponse == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(specialityResponse, HttpStatus.CREATED);
    }

}
