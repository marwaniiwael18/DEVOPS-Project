package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @Operation(description = "Add Instructor")
    @PostMapping("/add")
    public Instructor addInstructor(@Valid @RequestBody Instructor instructor){
        return  instructorServices.addInstructor(instructor);
    }
    @Operation(description = "Add Instructor and Assign To Course")
    @PutMapping("/addAndAssignToCourse/{numCourse}")
    public Instructor addAndAssignToInstructor(@RequestBody Instructor instructor, @PathVariable("numCourse")Long numCourse){
        return  instructorServices.addInstructorAndAssignToCourse(instructor,numCourse);
    }
    @Operation(description = "Retrieve all Instructors")
    @GetMapping("/all")
    public List<Instructor> getAllInstructors(){
        return instructorServices.retrieveAllInstructors();
    }

    @Operation(description = "Update Instructor ")
    @PutMapping("/update")
    public Instructor updateInstructor(@RequestBody Instructor instructor){
        return  instructorServices.updateInstructor(instructor);
    }

    @Operation(description = "Retrieve Instructor by Id")
    @GetMapping("/get/{id-instructor}")
    public ResponseEntity<Instructor> getById(@PathVariable("id-instructor") Long numInstructor) {
        Instructor instructor = instructorServices.retrieveInstructor(numInstructor);
        if (instructor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(instructor);
    }
    @Operation(description = "Delete Instructor by Id")
    @DeleteMapping("/delete/{id-instructor}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable("id-instructor") Long numInstructor) {
        instructorServices.deleteInstructor(numInstructor);
        return ResponseEntity.noContent().build();
    }


}
