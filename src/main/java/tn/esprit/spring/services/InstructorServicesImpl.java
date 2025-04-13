package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IInstructorRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@Service
public class InstructorServicesImpl implements IInstructorServices{

    private IInstructorRepository instructorRepository;
    private ICourseRepository courseRepository;
    private static final Logger logger = LoggerFactory.getLogger(InstructorServicesImpl.class);

    @Override
    public Instructor addInstructor(Instructor instructor) {
        logger.info("Adding new instructor: {}", instructor);
        Instructor savedInstructor = instructorRepository.save(instructor);
        logger.info("Instructor added successfully: {}", savedInstructor);
        return savedInstructor;
    }

    @Override
    public List<Instructor> retrieveAllInstructors() {
        logger.info("Retrieving all instructors...");
        List<Instructor> instructors = instructorRepository.findAll();
        logger.info("Total instructors retrieved: {}", instructors.size());
        return instructors;
    }

    @Override
    public Instructor updateInstructor(Instructor instructor) {
        logger.info("Attempting to update instructor with ID: {}", instructor.getNumInstructor());

        return instructorRepository.findById(instructor.getNumInstructor())
                .map(existingInstructor -> {
                    Instructor updatedInstructor = instructorRepository.save(instructor);
                    logger.info("Instructor updated successfully: {}", updatedInstructor);
                    return updatedInstructor;
                })
                .orElseGet(() -> {
                    logger.warn("Instructor with ID {} not found, update failed", instructor.getNumInstructor());
                    return null;
                });
    }

    @Override
    public Instructor retrieveInstructor(Long numInstructor) {
        logger.info("Retrieving instructor with ID: {}", numInstructor);
        
        if (numInstructor == null) {
            logger.warn("Cannot retrieve instructor with null ID");
            return null;
        }

        return instructorRepository.findById(numInstructor)
                .map(instructor -> {
                    logger.info("Instructor retrieved successfully: {}", instructor);
                    return instructor;
                })
                .orElseGet(() -> {
                    logger.warn("Instructor with ID {} not found", numInstructor);
                    return null;
                });
    }

    @Override
    public Instructor addInstructorAndAssignToCourse(Instructor instructor, Long numCourse) {
        logger.info("Attempting to add instructor and assign to course ID: {}", numCourse);
        
        if (instructor == null || numCourse == null) {
            logger.warn("Cannot assign instructor to course - instructor or course ID is null");
            return null;
        }

        Course course = courseRepository.findById(numCourse).orElse(null);

        if (course == null) {
            logger.warn("Course with ID {} not found, cannot assign instructor", numCourse);
            return null;
        }

        Set<Course> courseSet = instructor.getCourses() != null ? instructor.getCourses() : new HashSet<>();
        courseSet.add(course);
        instructor.setCourses(courseSet);

        Instructor savedInstructor = instructorRepository.save(instructor);
        logger.info("Instructor assigned to course successfully: {}", savedInstructor);

        return savedInstructor;
    }

    @Override
    public void deleteInstructor(Long numInstructor) {
        logger.info("Attempting to delete instructor with ID: {}", numInstructor);
        
        if (numInstructor == null) {
            logger.warn("Cannot delete instructor with null ID");
            return;
        }
        
        if (instructorRepository.existsById(numInstructor)) {
            instructorRepository.deleteById(numInstructor);
            logger.info("Instructor with ID {} deleted successfully", numInstructor);
        } else {
            logger.warn("Instructor with ID {} not found, deletion failed", numInstructor);
        }
    }
}
