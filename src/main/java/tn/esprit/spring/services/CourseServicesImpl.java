package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.repositories.ICourseRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class CourseServicesImpl implements ICourseServices {

    @Autowired
    private ICourseRepository courseRepository;
    private static final Logger logger = LoggerFactory.getLogger(CourseServicesImpl.class);

    @Override
    public List<Course> retrieveAllCourses() {
        logger.info("Retrieving all courses");
        List<Course> courses = courseRepository.findAll();
        logger.info("Found {} courses", courses.size());
        return courses;
    }

    @Override
    public Course addCourse(Course course) {
        logger.info("Attempting to add course: {}", course);
        Course savedCourse = courseRepository.save(course);
        logger.info("Course added successfully: {}", savedCourse);
        return savedCourse;
    }

    @Override
    public Course updateCourse(Course course) {
        logger.info("Attempting to update course: {}", course);
        
        if (course == null) {
            logger.warn("Cannot update null course");
            return null;
        }

        return courseRepository.findById(course.getNumCourse())
                .map(existingCourse -> {
                    Course updatedCourse = courseRepository.save(course);
                    logger.info("Course updated successfully: {}", updatedCourse);
                    return updatedCourse;
                })
                .orElseGet(() -> {
                    logger.warn("Course with ID {} not found, update failed", course.getNumCourse());
                    return null;
                });
    }

    @Override
    public Course retrieveCourse(Long numCourse) {
        logger.info("Retrieving course with ID: {}", numCourse);
        
        if (numCourse == null) {
            logger.warn("Cannot retrieve course with null ID");
            return null;
        }

        return courseRepository.findById(numCourse)
                .map(course -> {
                    logger.info("Course retrieved successfully: {}", course);
                    return course;
                })
                .orElseGet(() -> {
                    logger.warn("Course with ID {} not found", numCourse);
                    return null;
                });
    }

    @Override
    public void deleteCourse(Long numCourse) {
        logger.info("Attempting to delete course with ID: {}", numCourse);
        
        if (numCourse == null) {
            logger.warn("Cannot delete course with null ID");
            return;
        }

        if (courseRepository.existsById(numCourse)) {
            courseRepository.deleteById(numCourse);
            logger.info("Course with ID {} deleted successfully", numCourse);
        } else {
            logger.warn("Course with ID {} not found, deletion failed", numCourse);
        }
    }

}
