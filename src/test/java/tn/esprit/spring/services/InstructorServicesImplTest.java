package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IInstructorRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorServicesImplTest {

    // Define constants for repeated values
    private static final String INSTRUCTOR_FIRST_NAME = "John";
    private static final String INSTRUCTOR_LAST_NAME = "Doe";
    private static final String INSTRUCTOR_FIRST_NAME_UPDATED = "Johnny";
    private static final LocalDate INSTRUCTOR_HIRE_DATE = LocalDate.of(2022, 1, 1);
    private static final LocalDate INSTRUCTOR_HIRE_DATE_UPDATED = LocalDate.of(2023, 2, 15);

    @Mock
    private IInstructorRepository instructorRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private InstructorServicesImpl instructorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddInstructor() {
        Instructor instructor = new Instructor(null, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>());
        Instructor savedInstructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>());

        when(instructorRepository.save(any(Instructor.class))).thenReturn(savedInstructor);

        Instructor result = instructorService.addInstructor(instructor);

        assertNotNull(result);
        assertEquals(1L, result.getNumInstructor());
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }

    @Test
    void testRetrieveAllInstructors() {
        List<Instructor> instructors = Arrays.asList(
                new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>()),
                new Instructor(2L, "Jane", "Smith", LocalDate.of(2021, 5, 10), new HashSet<>())
        );

        when(instructorRepository.findAll()).thenReturn(instructors);

        List<Instructor> retrievedInstructors = instructorService.retrieveAllInstructors();

        assertNotNull(retrievedInstructors);
        assertEquals(2, retrievedInstructors.size());
        verify(instructorRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveInstructorFound() {
        Instructor instructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>());
        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));

        Instructor retrievedInstructor = instructorService.retrieveInstructor(1L);

        assertNotNull(retrievedInstructor);
        assertEquals(1L, retrievedInstructor.getNumInstructor());
        verify(instructorRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveInstructorNotFound() {
        when(instructorRepository.findById(anyLong())).thenReturn(Optional.empty());

        Instructor retrievedInstructor = instructorService.retrieveInstructor(99L);

        assertNull(retrievedInstructor);
        verify(instructorRepository, times(1)).findById(99L);
    }

    @Test
    void testUpdateInstructor() {
        Instructor existingInstructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>());
        Instructor updatedInstructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME_UPDATED, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE_UPDATED, new HashSet<>());

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(existingInstructor));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(updatedInstructor);

        Instructor result = instructorService.updateInstructor(updatedInstructor);

        assertNotNull(result);
        assertEquals(INSTRUCTOR_FIRST_NAME_UPDATED, result.getFirstName());
        assertEquals(INSTRUCTOR_HIRE_DATE_UPDATED, result.getDateOfHire());
        verify(instructorRepository, times(1)).findById(1L);
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }

    @Test
    void testUpdateInstructorNotFound() {
        Instructor updatedInstructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME_UPDATED, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE_UPDATED, new HashSet<>());
        when(instructorRepository.findById(1L)).thenReturn(Optional.empty());

        Instructor result = instructorService.updateInstructor(updatedInstructor);

        assertNull(result);
        verify(instructorRepository, times(1)).findById(1L);
        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    void testAddInstructorAndAssignToCourse() {
        Instructor instructor = new Instructor(null, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>());
        Course course = new Course(1L, 3, null, null, 120.0f, 5, null);
        Instructor savedInstructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>(Collections.singleton(course)));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(savedInstructor);

        Instructor result = instructorService.addInstructorAndAssignToCourse(instructor, 1L);

        assertNotNull(result);
        assertEquals(1, result.getCourses().size());
        assertTrue(result.getCourses().contains(course));
        verify(courseRepository, times(1)).findById(1L);
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }

    @Test
    void testAddInstructorAndAssignToCourseNotFound() {
        Instructor instructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>());
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        Instructor result = instructorService.addInstructorAndAssignToCourse(instructor, 99L);

        assertNull(result);
        verify(courseRepository, times(1)).findById(99L);
        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    void testAddInstructorAndAssignToCourseInstructorAlreadyHasCourses() {
        Instructor instructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>());
        Course existingCourse = new Course(2L, 3, null, null, 120.0f, 5, null);
        Course newCourse = new Course(1L, 3, null, null, 120.0f, 5, null);

        instructor.getCourses().add(existingCourse);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(newCourse));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor result = instructorService.addInstructorAndAssignToCourse(instructor, 1L);

        assertNotNull(result);
        assertEquals(2, result.getCourses().size());
        verify(courseRepository, times(1)).findById(1L);
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }

    @Test
    void testAddInstructorWithNullValues() {
        Instructor instructor = new Instructor(null, null, null, null, null);

        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor result = instructorService.addInstructor(instructor);

        assertNotNull(result);
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }

    @Test
    void testUpdateInstructorWithNullValues() {
        Instructor existingInstructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>());
        Instructor updatedInstructor = new Instructor(1L, null, null, null, null);

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(existingInstructor));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(updatedInstructor);

        Instructor result = instructorService.updateInstructor(updatedInstructor);

        assertNotNull(result);
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
        verify(instructorRepository, times(1)).findById(1L);
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }

    @Test
    void testRetrieveInstructorWithNullId() {
        Instructor result = instructorService.retrieveInstructor(null);

        assertNull(result);
        verify(instructorRepository, never()).findById(anyLong());
    }

    @Test
    void testAddInstructorAndAssignToCourseWithMaxCourses() {
        Instructor instructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>());
        Course course1 = new Course(1L, 3, null, null, 120.0f, 5, null);
        Course course2 = new Course(2L, 3, null, null, 120.0f, 5, null);
        Course course3 = new Course(3L, 3, null, null, 120.0f, 5, null);

        instructor.getCourses().add(course1);
        instructor.getCourses().add(course2);

        when(courseRepository.findById(3L)).thenReturn(Optional.of(course3));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor result = instructorService.addInstructorAndAssignToCourse(instructor, 3L);

        assertNotNull(result);
        assertEquals(3, result.getCourses().size());
        verify(courseRepository, times(1)).findById(3L);
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }

    @Test
    void testAddInstructorAndAssignToCourseWithNullCourses() {
        Instructor instructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, null);
        Course course = new Course(1L, 3, null, null, 120.0f, 5, null);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor result = instructorService.addInstructorAndAssignToCourse(instructor, 1L);

        assertNotNull(result);
        assertEquals(1, result.getCourses().size());
        verify(courseRepository, times(1)).findById(1L);
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }

    @Test
    void testAddInstructorAndAssignToCourseCourseNotFound() {
        Instructor instructor = new Instructor(null, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>());

        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        Instructor result = instructorService.addInstructorAndAssignToCourse(instructor, 99L);

        assertNull(result);
        verify(courseRepository, times(1)).findById(99L);
        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    void testAddInstructorAndAssignToCourseWithExistingCourses() {
        Course existingCourse = new Course(2L, 4, null, null, 150.0f, 10, null);
        Instructor instructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>(Collections.singleton(existingCourse)));
        Course newCourse = new Course(1L, 3, null, null, 120.0f, 5, null);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(newCourse));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor result = instructorService.addInstructorAndAssignToCourse(instructor, 1L);

        assertNotNull(result);
        assertEquals(2, result.getCourses().size());
        assertTrue(result.getCourses().contains(existingCourse));
        assertTrue(result.getCourses().contains(newCourse));
        verify(courseRepository, times(1)).findById(1L);
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }
}