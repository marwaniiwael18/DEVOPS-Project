package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IRegistrationRepository;
import tn.esprit.spring.repositories.ISkierRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class RegistrationServicesImpl implements IRegistrationServices {

    private final IRegistrationRepository registrationRepository;
    private final ISkierRepository skierRepository;
    private final ICourseRepository courseRepository;

    @Override
    public Registration addRegistrationAndAssignToSkier(Registration registration, Long numSkier) {
        Skier skier = skierRepository.findById(numSkier).orElse(null);
        if (skier == null) {
            log.warn("Skier with id {} not found.", numSkier);
            return null;
        }
        registration.setSkier(skier);
        return registrationRepository.save(registration);
    }

    @Override
    public Registration assignRegistrationToCourse(Long numRegistration, Long numCourse) {
        Registration registration = registrationRepository.findById(numRegistration).orElse(null);
        Course course = courseRepository.findById(numCourse).orElse(null);

        if (registration == null || course == null) {
            log.warn("Registration or Course not found. Registration ID: {}, Course ID: {}", numRegistration, numCourse);
            return null;
        }

        registration.setCourse(course);
        return registrationRepository.save(registration);
    }

    @Transactional
    @Override
    public Registration addRegistrationAndAssignToSkierAndCourse(Registration registration, Long numSkieur, Long numCours) {
        Skier skier = skierRepository.findById(numSkieur).orElse(null);
        Course course = courseRepository.findById(numCours).orElse(null);

        if (skier == null || course == null) {
            log.warn("Skier or Course not found. Skier ID: {}, Course ID: {}", numSkieur, numCours);
            return null;
        }

        if (isAlreadyRegistered(registration, skier, course)) {
            log.info("Skier is already registered for this course in week: {}", registration.getNumWeek());
            return null;
        }

        int ageSkieur = calculateAge(skier.getDateOfBirth());
        log.info("Skier age: {}", ageSkieur);

        return handleCourseRegistration(registration, skier, course, ageSkieur);
    }

    private boolean isAlreadyRegistered(Registration registration, Skier skier, Course course) {
        return registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(
                registration.getNumWeek(), skier.getNumSkier(), course.getNumCourse()) >= 1;
    }

    private int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    private Registration handleCourseRegistration(Registration registration, Skier skier, Course course, int ageSkieur) {
        switch (course.getTypeCourse()) {
            case INDIVIDUAL:
                return handleIndividualCourse(registration, skier, course);

            case COLLECTIVE_CHILDREN:
                return handleCollectiveChildrenCourse(registration, skier, course, ageSkieur);

            case COLLECTIVE_ADULT:
                return handleCollectiveAdultCourse(registration, skier, course, ageSkieur);

            default:
                log.warn("Unsupported course type: {}", course.getTypeCourse());
                return null;
        }
    }

    private Registration handleIndividualCourse(Registration registration, Skier skier, Course course) {
        log.info("Adding registration for individual course without tests.");
        return assignRegistration(registration, skier, course);
    }

    private Registration handleCollectiveChildrenCourse(Registration registration, Skier skier, Course course, int ageSkieur) {
        if (ageSkieur < 16) {
            log.info("Skier is a child. Proceeding with registration.");

            if (isCourseFull(course, registration.getNumWeek())) {
                log.info("Course is full for week: {}", registration.getNumWeek());
                return null;
            }

            log.info("Course successfully added for child skier.");
            return assignRegistration(registration, skier, course);
        } else {
            log.info("Skier is too old for a children's course. Age: {}", ageSkieur);
            return null;
        }
    }

    private Registration handleCollectiveAdultCourse(Registration registration, Skier skier, Course course, int ageSkieur) {
        if (ageSkieur >= 16) {
            log.info("Skier is an adult. Proceeding with registration.");

            if (isCourseFull(course, registration.getNumWeek())) {
                log.info("Course is full for week: {}", registration.getNumWeek());
                return null;
            }

            log.info("Course successfully added for adult skier.");
            return assignRegistration(registration, skier, course);
        } else {
            log.info("Skier is too young for an adult course. Age: {}", ageSkieur);
            return null;
        }
    }

    private boolean isCourseFull(Course course, int numWeek) {
        return registrationRepository.countByCourseAndNumWeek(course, numWeek) >= 6;
    }

    private Registration assignRegistration(Registration registration, Skier skier, Course course) {
        registration.setSkier(skier);
        registration.setCourse(course);
        return registrationRepository.save(registration);
    }

    @Override
    public List<Integer> numWeeksCourseOfInstructorBySupport(Long numInstructor, Support support) {
        return registrationRepository.numWeeksCourseOfInstructorBySupport(numInstructor, support);
    }
}