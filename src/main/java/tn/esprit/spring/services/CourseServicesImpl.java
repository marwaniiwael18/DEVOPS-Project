package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.repositories.ICourseRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class CourseServicesImpl implements ICourseServices {

    private final ICourseRepository courseRepository;
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
        Course updatedCourse = courseRepository.save(course);
        logger.info("Course updated successfully: {}", updatedCourse);
        return updatedCourse;
    }

    @Override
    public Course retrieveCourse(Long numCourse) {
        logger.info("Retrieving course with ID: {}", numCourse);
        return courseRepository.findById(numCourse).orElse(null);
    }
}
