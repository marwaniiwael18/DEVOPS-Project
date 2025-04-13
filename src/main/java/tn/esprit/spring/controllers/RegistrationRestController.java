package tn.esprit.spring.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dto.RegistrationDTO;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.mappers.RegistrationMapper;
import tn.esprit.spring.services.IRegistrationServices;

import java.util.List;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationRestController {

    private final IRegistrationServices registrationServices;
    private final RegistrationMapper registrationMapper;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationRestController.class);

    @PostMapping("/addAndAssignToSkier/{skierId}")
    public ResponseEntity<RegistrationDTO> addAndAssignToSkier(@RequestBody RegistrationDTO dto, @PathVariable Long skierId) {
        try {
            Registration registration = registrationMapper.toEntity(dto);
            Registration savedRegistration = registrationServices.addRegistrationAndAssignToSkier(registration, skierId);
            if (savedRegistration == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(registrationMapper.toDTO(savedRegistration));
        } catch (Exception e) {
            logger.error("Error adding registration and assigning to skier: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/assignToCourse/{numRegistration}/{numCourse}")
    public ResponseEntity<RegistrationDTO> assignToCourse(
            @PathVariable Long numRegistration,
            @PathVariable Long numCourse) {
        try {
            Registration registration = registrationServices.assignRegistrationToCourse(numRegistration, numCourse);
            if (registration == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(registrationMapper.toDTO(registration));
        } catch (Exception e) {
            logger.error("Error assigning registration to course: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/addAndAssignToSkierAndCourse/{numSkier}/{numCourse}")
    public ResponseEntity<RegistrationDTO> addAndAssignToSkierAndCourse(
            @RequestBody RegistrationDTO dto,
            @PathVariable Long numSkier,
            @PathVariable Long numCourse) {
        try {
            Registration registration = registrationMapper.toEntity(dto);
            Registration savedRegistration = registrationServices.addRegistrationAndAssignToSkierAndCourse(
                    registration, numSkier, numCourse);
            if (savedRegistration == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(registrationMapper.toDTO(savedRegistration));
        } catch (Exception e) {
            logger.error("Error adding registration and assigning to skier and course: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/numWeeks/{numInstructor}/{support}")
    public ResponseEntity<List<Integer>> numWeeksCourseOfInstructorBySupport(
            @PathVariable Long numInstructor,
            @PathVariable Support support) {
        try {
            List<Integer> weeks = registrationServices.numWeeksCourseOfInstructorBySupport(numInstructor, support);
            return ResponseEntity.ok(weeks);
        } catch (Exception e) {
            logger.error("Error retrieving weeks by instructor and support: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
