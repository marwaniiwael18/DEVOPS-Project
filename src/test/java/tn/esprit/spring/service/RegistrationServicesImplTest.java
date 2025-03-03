package tn.esprit.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IRegistrationRepository;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.services.RegistrationServicesImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServicesImplTest {

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private RegistrationServicesImpl registrationServices;

    private Registration registration;
    private Skier skier;
    private Course course;

    @BeforeEach
    void setUp() {
        // Setup test data
        registration = new Registration();
        registration.setNumRegistration(1L);
        registration.setNumWeek(1);

        skier = new Skier();
        skier.setNumSkier(1L);
        skier.setDateOfBirth(LocalDate.now().minusYears(20)); // 20 years old

        course = new Course();
        course.setNumCourse(1L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_ADULT);
        course.setSupport(Support.SKI);

        Instructor instructor = new Instructor();
        instructor.setNumInstructor(1L);
        course.setInstructor(instructor);
    }

    @Test
    void testAddRegistrationAndAssignToSkier() {
        // Setup
        when(skierRepository.findById(anyLong())).thenReturn(Optional.of(skier));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        // Execute
        Registration result = registrationServices.addRegistrationAndAssignToSkier(registration, 1L);

        // Verify
        assertNotNull(result);
        assertEquals(skier, result.getSkier());
    }

    @Test
    void testAssignRegistrationToCourse() {
        // Setup
        when(registrationRepository.findById(anyLong())).thenReturn(Optional.of(registration));
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        // Execute
        Registration result = registrationServices.assignRegistrationToCourse(1L, 1L);

        // Verify
        assertNotNull(result);
        assertEquals(course, result.getCourse());
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_Success() {
        // Setup - Adult registering for adult course
        when(skierRepository.findById(anyLong())).thenReturn(Optional.of(skier));
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(anyInt(), anyLong(), anyLong())).thenReturn(0L);
        when(registrationRepository.countByCourseAndNumWeek(any(Course.class), anyInt())).thenReturn(3L); // 3 registrations already
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        // Execute
        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        // Verify
        assertNotNull(result);
        assertEquals(skier, result.getSkier());
        assertEquals(course, result.getCourse());
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_AlreadyRegistered() {
        // Setup - Already registered for this course and week
        when(skierRepository.findById(anyLong())).thenReturn(Optional.of(skier));
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(anyInt(), anyLong(), anyLong())).thenReturn(1L);

        // Execute
        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        // Verify
        assertNull(result);
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_CourseFull() {
        // Setup - Course is full (6 registrations)
        when(skierRepository.findById(anyLong())).thenReturn(Optional.of(skier));
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(anyInt(), anyLong(), anyLong())).thenReturn(0L);
        when(registrationRepository.countByCourseAndNumWeek(any(Course.class), anyInt())).thenReturn(6L);

        // Execute
        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        // Verify
        assertNull(result);
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_AgeRestrictionForChildCourse() {
        // Setup - Adult trying to register for children's course
        Course childCourse = new Course();
        childCourse.setNumCourse(2L);
        childCourse.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);

        when(skierRepository.findById(anyLong())).thenReturn(Optional.of(skier)); // Adult skier
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(childCourse));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(anyInt(), anyLong(), anyLong())).thenReturn(0L);

        // Execute
        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 2L);

        // Verify
        assertNotNull(result); // Returns registration without saving it
        assertNull(result.getSkier());
        assertNull(result.getCourse());
    }

    @Test
    void testNumWeeksCourseOfInstructorBySupport() {
        // Setup
        List<Integer> expectedWeeks = Arrays.asList(1, 2, 3);
        when(registrationRepository.numWeeksCourseOfInstructorBySupport(anyLong(), any(Support.class)))
                .thenReturn(expectedWeeks);

        // Execute
        List<Integer> result = registrationServices.numWeeksCourseOfInstructorBySupport(1L, Support.SKI);

        // Verify
        assertEquals(expectedWeeks, result);
        assertEquals(3, result.size());
    }
}