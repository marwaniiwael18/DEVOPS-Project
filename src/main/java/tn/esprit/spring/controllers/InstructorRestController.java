package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dto.InstructorDTO;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.mappers.InstructorMapper;
import tn.esprit.spring.services.IInstructorServices;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "\uD83D\uDC69\u200D\uD83C\uDFEB Instructor Management")
@RestController
@RequestMapping("/instructor")
@RequiredArgsConstructor
public class InstructorRestController {

    private final IInstructorServices instructorServices;
    private final InstructorMapper instructorMapper;
    private static final Logger logger = LoggerFactory.getLogger(InstructorRestController.class);

    @Operation(description = "Add Instructor")
    @PostMapping("/add")
    public ResponseEntity<InstructorDTO> addInstructor(@RequestBody InstructorDTO instructorDTO) {
        try {
            // Add validation logic to match test expectations
            if (instructorDTO == null || 
                instructorDTO.getFirstName() == null || 
                instructorDTO.getFirstName().trim().isEmpty() ||
                instructorDTO.getLastName() == null || 
                instructorDTO.getLastName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Instructor instructor = instructorMapper.toEntity(instructorDTO);
            Instructor savedInstructor = instructorServices.addInstructor(instructor);
            return ResponseEntity.ok(instructorMapper.toDTO(savedInstructor));
        } catch (Exception e) {
            logger.error("Error adding instructor: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(description = "Add Instructor and Assign To Course")
    @PutMapping("/addAndAssignToCourse/{numCourse}")
    public ResponseEntity<InstructorDTO> addAndAssignToInstructor(@RequestBody InstructorDTO instructorDTO, @PathVariable("numCourse") String numCourse) {
        try {
            Long courseId = Long.parseLong(numCourse);
            Instructor instructor = instructorMapper.toEntity(instructorDTO);
            Instructor result = instructorServices.addInstructorAndAssignToCourse(instructor, courseId);
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(instructorMapper.toDTO(result));
        } catch (NumberFormatException e) {
            logger.error("Invalid course ID format: {}", numCourse);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(description = "Retrieve all Instructors")
    @GetMapping("/all")
    public List<InstructorDTO> getAllInstructors(){
        return instructorServices.retrieveAllInstructors().stream()
                .map(instructorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(description = "Update Instructor ")
    @PutMapping("/update")
    public ResponseEntity<InstructorDTO> updateInstructor(@RequestBody InstructorDTO instructorDTO) {
        try {
            Instructor instructor = instructorMapper.toEntity(instructorDTO);
            Instructor result = instructorServices.updateInstructor(instructor);
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(instructorMapper.toDTO(result));
        } catch (Exception e) {
            logger.error("Error updating instructor: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(description = "Retrieve Instructor by Id")
    @GetMapping("/get/{id-instructor}")
    public ResponseEntity<InstructorDTO> getById(@PathVariable("id-instructor") String numInstructor) {
        try {
            Long instructorId = Long.parseLong(numInstructor);
            Instructor instructor = instructorServices.retrieveInstructor(instructorId);
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(instructorMapper.toDTO(instructor));
        } catch (NumberFormatException e) {
            logger.error("Invalid instructor ID format: {}", numInstructor);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(description = "Delete Instructor by Id")
    @DeleteMapping("/delete/{id-instructor}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable("id-instructor") String numInstructor) {
        try {
            Long instructorId = Long.parseLong(numInstructor);
            instructorServices.deleteInstructor(instructorId);
            return ResponseEntity.noContent().build();
        } catch (NumberFormatException e) {
            logger.error("Invalid instructor ID format: {}", numInstructor);
            return ResponseEntity.badRequest().build();
        }
    }
}
