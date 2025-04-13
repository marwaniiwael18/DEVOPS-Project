package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.services.IInstructorServices;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "\uD83D\uDC69\u200D\uD83C\uDFEB Instructor Management")
@RestController
@RequestMapping("/instructor")
@RequiredArgsConstructor
public class InstructorRestController {

    private final IInstructorServices instructorServices;
    private static final Logger logger = LoggerFactory.getLogger(InstructorRestController.class);

    @Operation(description = "Add Instructor")
    @PostMapping("/add")
    public Instructor addInstructor(@Valid @RequestBody Instructor instructor){
        return instructorServices.addInstructor(instructor);
    }
    
    @Operation(description = "Add Instructor and Assign To Course")
    @PutMapping("/addAndAssignToCourse/{numCourse}")
    public ResponseEntity<Instructor> addAndAssignToInstructor(@RequestBody Instructor instructor, @PathVariable("numCourse") String numCourse) {
        try {
            Long courseId = Long.parseLong(numCourse);
            Instructor result = instructorServices.addInstructorAndAssignToCourse(instructor, courseId);
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (NumberFormatException e) {
            logger.error("Invalid course ID format: {}", numCourse);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(description = "Retrieve all Instructors")
    @GetMapping("/all")
    public List<Instructor> getAllInstructors(){
        return instructorServices.retrieveAllInstructors();
    }

    @Operation(description = "Update Instructor ")
    @PutMapping("/update")
    public ResponseEntity<Instructor> updateInstructor(@RequestBody Instructor instructor){
        Instructor result = instructorServices.updateInstructor(instructor);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @Operation(description = "Retrieve Instructor by Id")
    @GetMapping("/get/{id-instructor}")
    public ResponseEntity<Instructor> getById(@PathVariable("id-instructor") String numInstructor) {
        try {
            Long instructorId = Long.parseLong(numInstructor);
            Instructor instructor = instructorServices.retrieveInstructor(instructorId);
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(instructor);
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
