package tn.esprit.spring.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IInstructorRepository;
import tn.esprit.spring.repositories.IRegistrationRepository;
import tn.esprit.spring.repositories.ISkierRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IRegistrationRepositoryTest {

    @Autowired
    private IRegistrationRepository registrationRepository;

    @Autowired
    private ICourseRepository courseRepository;

    @Autowired
    private ISkierRepository skierRepository;

    @Autowired
    private IInstructorRepository instructorRepository;

    @Test
    public void testCountByCourseAndNumWeek() {
        // Create and save instructor
        Instructor instructor = new Instructor();
        instructor.setFirstName("John");
        instructor.setLastName("Doe");
        instructorRepository.save(instructor);

        // Create and save course
        Course course = new Course();
        course.setLevel(2); // If Level is an Enum, change Course class to use Enum instead of int
        course.setTypeCourse(TypeCourse.COLLECTIVE_ADULT);
        course.setSupport(Support.SKI);
        course.setInstructor(instructor);
        courseRepository.save(course);

        // Create and save skier
        Skier skier = new Skier();
        skier.setFirstName("Jane");
        skier.setLastName("Smith");
        skier.setDateOfBirth(LocalDate.of(2000, 1, 1));
        skier.setCity("AlpineCity");
        skierRepository.save(skier);

        // Create and save registrations
        Registration registration1 = new Registration();
        registration1.setNumWeek(5);
        registration1.setCourse(course);
        registration1.setSkier(skier);
        registrationRepository.save(registration1);

        Registration registration2 = new Registration();
        registration2.setNumWeek(5);
        registration2.setCourse(course);
        registration2.setSkier(skier);
        registrationRepository.save(registration2);

        // Test method
        long count = registrationRepository.countByCourseAndNumWeek(course, 5);

        // Assertions
        assertEquals(2, count);
    }

    @Test
    public void testNumWeeksCourseOfInstructorBySupport() {
        // Create and save instructor
        Instructor instructor = new Instructor();
        instructor.setFirstName("John");
        instructor.setLastName("Doe");
        instructorRepository.save(instructor);

        // Create and save courses
        Course course1 = new Course();
        course1.setLevel(2);
        course1.setTypeCourse(TypeCourse.COLLECTIVE_ADULT);
        course1.setSupport(Support.SKI);
        course1.setInstructor(instructor);
        courseRepository.save(course1);

        Course course2 = new Course();
        course2.setLevel(3);
        course2.setTypeCourse(TypeCourse.INDIVIDUAL);
        course2.setSupport(Support.SNOWBOARD);
        course2.setInstructor(instructor);
        courseRepository.save(course2);

        // Create and save skier
        Skier skier = new Skier();
        skier.setFirstName("Jane");
        skier.setLastName("Smith");
        skier.setDateOfBirth(LocalDate.of(2000, 1, 1));
        skier.setCity("AlpineCity");
        skierRepository.save(skier);

        // Create and save registrations
        Registration registration1 = new Registration();
        registration1.setNumWeek(1);
        registration1.setCourse(course1);
        registration1.setSkier(skier);
        registrationRepository.save(registration1);

        Registration registration2 = new Registration();
        registration2.setNumWeek(2);
        registration2.setCourse(course1);
        registration2.setSkier(skier);
        registrationRepository.save(registration2);

        Registration registration3 = new Registration();
        registration3.setNumWeek(3);
        registration3.setCourse(course2);
        registration3.setSkier(skier);
        registrationRepository.save(registration3);

        // Fetch instructor ID after saving
        Long instructorId = instructor.getNumInstructor();

        // Test method
        List<Integer> skiWeeks = registrationRepository.numWeeksCourseOfInstructorBySupport(
                instructorId, Support.SKI);

        // Assertions
        assertEquals(2, skiWeeks.size());
        assertTrue(skiWeeks.contains(1));
        assertTrue(skiWeeks.contains(2));

        List<Integer> snowboardWeeks = registrationRepository.numWeeksCourseOfInstructorBySupport(
                instructorId, Support.SNOWBOARD);

        assertEquals(1, snowboardWeeks.size());
        assertTrue(snowboardWeeks.contains(3));
    }

    @Test
    public void testCountDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse() {
        // Create and save instructor
        Instructor instructor = new Instructor();
        instructor.setFirstName("John");
        instructor.setLastName("Doe");
        instructorRepository.save(instructor);

        // Create and save course
        Course course = new Course();
        course.setLevel(2);
        course.setTypeCourse(TypeCourse.COLLECTIVE_ADULT);
        course.setSupport(Support.SKI);
        course.setInstructor(instructor);
        courseRepository.save(course);

        // Create and save skier
        Skier skier = new Skier();
        skier.setFirstName("Jane");
        skier.setLastName("Smith");
        skier.setDateOfBirth(LocalDate.of(2000, 1, 1));
        skier.setCity("AlpineCity");
        skierRepository.save(skier);

        // Create and save registration
        Registration registration = new Registration();
        registration.setNumWeek(7);
        registration.setCourse(course);
        registration.setSkier(skier);
        registrationRepository.save(registration);

        // Fetch IDs
        Long skierId = skier.getNumSkier();
        Long courseId = course.getNumCourse();

        // Test method
        long count = registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(
                7, skierId, courseId);

        // Assertions
        assertEquals(1, count);

        long nonExistentCount = registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(
                8, skierId, courseId);

        assertEquals(0, nonExistentCount);
    }
}
