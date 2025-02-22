package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.services.ICourseServices;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "\uD83D\uDCDA Course Management")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseRestController {
    
    private final ICourseServices courseServices;

    @Operation(description = "Add Course")
    @PostMapping("/add")
    public ResponseEntity<Course> addCourse(@Valid @RequestBody Course course) {
        Course savedCourse = courseServices.addCourse(course);
        return ResponseEntity.ok(savedCourse);
    }
    @Operation(description = "Retrieve all Courses")
    @GetMapping("/all")
    public List<Course> getAllCourses(){
        return courseServices.retrieveAllCourses();
    }

    @Operation(description = "Update Course ")
    @PutMapping("/update")
    public ResponseEntity<Course> updateCourse(@RequestBody Course course) {
        Course updatedCourse = courseServices.updateCourse(course);
        if (updatedCourse == null) {
            return ResponseEntity.notFound().build(); // Retourne 404 si le cours n'existe pas
        }
        return ResponseEntity.ok(updatedCourse);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
    @Operation(description = "Retrieve Course by Id")
    @GetMapping("/get/{id-course}")
    public ResponseEntity<Course> getById(@PathVariable("id-course") Long numCourse) {
        Course course = courseServices.retrieveCourse(numCourse);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Retourne 404 si le cours n'est pas trouvé
        }
        return ResponseEntity.ok(course);  // Retourne le cours avec un statut 200
    }


}
