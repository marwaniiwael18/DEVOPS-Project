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

@Tag(name = "📚 Course Management")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseRestController {

    private final ICourseServices courseServices;
    private static final Logger logger = LoggerFactory.getLogger(CourseRestController.class);

    @Operation(description = "Add Course")
    @PostMapping("/add")
    public ResponseEntity<Course> addCourse(@Valid @RequestBody Course course) {
        try {
            logger.info("📌 [ADD] Request to add a new course: {}", course);
            Course savedCourse = courseServices.addCourse(course);
            logger.info("✅ Course successfully added: {}", savedCourse);
            return ResponseEntity.ok(savedCourse);
        } catch (Exception e) {
            logger.error("❌ Error adding course: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Retrieve all Courses")
    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllCourses() {
        logger.info("📌 [GET ALL] Retrieving all courses...");
        List<Course> courses = courseServices.retrieveAllCourses();
        logger.info("✅ Total courses found: {}", courses.size());
        return ResponseEntity.ok(courses);
    }

    @Operation(description = "Update Course")
    @PutMapping("/update")
    public ResponseEntity<Course> updateCourse(@Valid @RequestBody Course course) {
        logger.info("📌 [UPDATE] Request to update course: {}", course);
        Course updatedCourse = courseServices.updateCourse(course);
        if (updatedCourse == null) {
            logger.warn("⚠️ Course not found for update: {}", course);
            return ResponseEntity.notFound().build();
        }
        logger.info("✅ Course successfully updated: {}", updatedCourse);
        return ResponseEntity.ok(updatedCourse);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Course> getById(@PathVariable("id") String numCourse) {
        logger.info("📌 [GET BY ID] Retrieving course with ID: {}", numCourse);
        try {
            Long id = Long.parseLong(numCourse);
            Course course = courseServices.retrieveCourse(id);
            if (course == null) {
                logger.warn("⚠️ Course not found with ID: {}", numCourse);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            logger.info("✅ Course retrieved successfully: {}", course);
            return ResponseEntity.ok(course);
        } catch (NumberFormatException e) {
            logger.error("❌ Invalid course ID format: {}", numCourse, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Operation(description = "Delete Course by ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id") String numCourse) {
        logger.info("📌 [DELETE] Request received to delete course with ID: {}", numCourse);
        
        try {
            Long courseId = Long.parseLong(numCourse);
            if (courseServices.retrieveCourse(courseId) != null) {
                courseServices.deleteCourse(courseId);
                logger.info("✅ Course successfully deleted with ID: {}", courseId);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("⚠️ Course with ID {} not found", courseId);
                return ResponseEntity.notFound().build();
            }
        } catch (NumberFormatException e) {
            logger.error("❌ Invalid course ID format: {}", numCourse);
            return ResponseEntity.badRequest().build();
        }
    }

}
