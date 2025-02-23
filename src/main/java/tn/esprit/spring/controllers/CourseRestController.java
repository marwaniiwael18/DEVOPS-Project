package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.services.ICourseServices;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "\uD83D\uDCDA Course Management")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseRestController {

    private final ICourseServices courseServices;
    private static final Logger logger = LoggerFactory.getLogger(CourseRestController.class);

    @Operation(description = "Add Course")
    @PostMapping("/add")
    public ResponseEntity<Course> addCourse(@Valid @RequestBody Course course) {
        logger.info("Attempting to add a new course: {}", course);
        Course savedCourse = courseServices.addCourse(course);
        logger.info("Successfully added course: {}", savedCourse);
        return ResponseEntity.ok(savedCourse);
    }

    @Operation(description = "Retrieve all Courses")
    @GetMapping("/all")
    public List<Course> getAllCourses() {
        logger.info("Retrieving all courses");
        List<Course> courses = courseServices.retrieveAllCourses();
        logger.info("Found {} courses", courses.size());
        return courses;
    }

    @Operation(description = "Update Course")
    @PutMapping("/update")
    public ResponseEntity<Course> updateCourse(@Valid @RequestBody Course course) {
        logger.info("Attempting to update course: {}", course);
        Course updatedCourse = courseServices.updateCourse(course);
        if (updatedCourse == null) {
            logger.warn("Course not found for update: {}", course);
            return ResponseEntity.notFound().build();
        }
        logger.info("Successfully updated course: {}", updatedCourse);
        return ResponseEntity.ok(updatedCourse);
    }

    @GetMapping("/get/{id-course}")
    public ResponseEntity<Course> getById(@PathVariable("id-course") String numCourse) {
        logger.info("Retrieving course with ID: {}", numCourse);
        try {
            Long id = Long.parseLong(numCourse);
            Course course = courseServices.retrieveCourse(id);
            if (course == null) {
                logger.warn("Course not found with ID: {}", numCourse);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(course);
        } catch (NumberFormatException e) {
            logger.error("Invalid course ID format: {}", numCourse, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @Operation(description = "Delete Course by ID")
    @DeleteMapping("/delete/{id-course}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id-course") Long numCourse) {
        logger.info("Request received to delete course with ID: {}", numCourse);

        if (courseServices.retrieveCourse(numCourse) != null) {
            courseServices.deleteCourse(numCourse);
            logger.info("Successfully deleted course with ID: {}", numCourse);
            return ResponseEntity.ok().build();
        } else {
            logger.warn("Course with ID {} not found", numCourse);
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Internal server error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
