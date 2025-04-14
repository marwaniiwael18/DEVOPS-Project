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
                return ResponseEntity.status(404).build(); // Return 404 if not found
            }
            return ResponseEntity.ok(registrationMapper.toDTO(savedRegistration));
        } catch (Exception e) {
            logger.error("Error adding registration and assigning to skier: {}", e.getMessage());
            return ResponseEntity.status(500).build(); // Return 500 for server errors
        }
    }

    @PutMapping("/assignToCourse/{numRegistration}/{numCourse}")
    public ResponseEntity<RegistrationDTO> assignToCourse(
            @PathVariable Long numRegistration,
            @PathVariable Long numCourse) {
        try {
            Registration registration = registrationServices.assignRegistrationToCourse(numRegistration, numCourse);
            if (registration == null) {
                return ResponseEntity.status(404).build(); // Return 404 if not found
            }
            return ResponseEntity.ok(registrationMapper.toDTO(registration));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input for assigning registration to course: {}", e.getMessage());
            return ResponseEntity.status(400).build(); // Return 400 for bad input
        } catch (Exception e) {
            logger.error("Error assigning registration to course: {}", e.getMessage());
            return ResponseEntity.status(500).build(); // Return 500 for server errors
        }
    }

    @PostMapping("/addAndAssignToSkierAndCourse/{numSkier}/{numCourse}")
    public ResponseEntity<RegistrationDTO> addAndAssignToSkierAndCourse(
            @RequestBody RegistrationDTO dto,
            @PathVariable Long numSkier,
            @PathVariable Long numCourse) {
        try {
            if (dto == null || dto.getNumWeek() == null || dto.getNumWeek() <= 0) {
                return ResponseEntity.badRequest().build(); // Return 400 for invalid input
            }
            Registration registration = registrationMapper.toEntity(dto);
            Registration savedRegistration = registrationServices.addRegistrationAndAssignToSkierAndCourse(
                    registration, numSkier, numCourse);
            if (savedRegistration == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(registrationMapper.toDTO(savedRegistration));
        } catch (Exception e) {
            logger.error("Error adding registration and assigning to skier and course: {}", e.getMessage());
            return ResponseEntity.status(500).build(); // Return 500 for server errors
        }
    }

    @GetMapping("/numWeeks/{numInstructor}/{support}")
    public ResponseEntity<List<Integer>> numWeeksCourseOfInstructorBySupport(
            @PathVariable Long numInstructor,
            @PathVariable Support support) {
        try {
            // Always invoke the service method, even for invalid input
            if (support == null) {
                throw new IllegalArgumentException("Invalid support type");
            }
            List<Integer> weeks = registrationServices.numWeeksCourseOfInstructorBySupport(numInstructor, support);
            return ResponseEntity.ok(weeks);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid support type: {}", e.getMessage());
            return ResponseEntity.status(400).build(); // Return 400 for invalid input
        } catch (Exception e) {
            logger.error("Error retrieving weeks by instructor and support: {}", e.getMessage());
            return ResponseEntity.status(500).build(); // Return 500 for server errors
        }
    }
}
