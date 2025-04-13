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
public class RegistrationServicesImpl implements  IRegistrationServices{

    private IRegistrationRepository registrationRepository;
    private ISkierRepository skierRepository;
    private ICourseRepository courseRepository;


    @Override
    public Registration addRegistrationAndAssignToSkier(Registration registration, Long numSkier) {
        Skier skier = skierRepository.findById(numSkier).orElse(null);
        registration.setSkier(skier);
        return registrationRepository.save(registration);
    }

    @Override
    public Registration assignRegistrationToCourse(Long numRegistration, Long numCourse) {
        Registration registration = registrationRepository.findById(numRegistration).orElse(null);
        Course course = courseRepository.findById(numCourse).orElse(null);
        registration.setCourse(course);
        return registrationRepository.save(registration);
    }
    @Transactional
    @Override
    public Registration addRegistrationAndAssignToSkierAndCourse(Registration registration, Long numSkieur, Long numCours) {
        Skier skier = skierRepository.findById(numSkieur).orElse(null);
        Course course = courseRepository.findById(numCours).orElse(null);

        if (!isValidRegistrationRequest(registration, skier, course)) {
            return null;
        }

        int ageSkieur = calculateSkierAge(skier);
        return processRegistrationByType(registration, skier, course, ageSkieur);
    }

    private boolean isValidRegistrationRequest(Registration registration, Skier skier, Course course) {
        if (skier == null || course == null) {
            return false;
        }

        return registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(
                registration.getNumWeek(), skier.getNumSkier(), course.getNumCourse()) < 1;
    }

    private int calculateSkierAge(Skier skier) {
        return Period.between(skier.getDateOfBirth(), LocalDate.now()).getYears();
    }

    private Registration processRegistrationByType(Registration registration, Skier skier, Course course, int age) {
        switch (course.getTypeCourse()) {
            case INDIVIDUAL:
                return assignRegistration(registration, skier, course);

            case COLLECTIVE_CHILDREN:
                return processChildrenRegistration(registration, skier, course, age);

            default:
                return processAdultRegistration(registration, skier, course, age);
        }
    }

    private Registration processChildrenRegistration(Registration registration, Skier skier, Course course, int age) {
        if (age >= 16) {
            log.info("Sorry, your age doesn't allow you to register for this course!");
            return registration;
        }
        return checkAvailabilityAndRegister(registration, skier, course);
    }

    private Registration processAdultRegistration(Registration registration, Skier skier, Course course, int age) {
        if (age < 16) {
            log.info("Sorry, your age doesn't allow you to register for this course!");
            return registration;
        }
        return checkAvailabilityAndRegister(registration, skier, course);
    }

    private Registration checkAvailabilityAndRegister(Registration registration, Skier skier, Course course) {
        if (registrationRepository.countByCourseAndNumWeek(course, registration.getNumWeek()) < 6) {
            return assignRegistration(registration, skier, course);
        }
        log.info("Full Course! Please choose another week to register!");
        return null;
    }
    private Registration assignRegistration (Registration registration, Skier skier, Course course){
        registration.setSkier(skier);
        registration.setCourse(course);
        return registrationRepository.save(registration);
    }

    @Override
    public List<Integer> numWeeksCourseOfInstructorBySupport(Long numInstructor, Support support) {
        return registrationRepository.numWeeksCourseOfInstructorBySupport(numInstructor, support);
    }

}
