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
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.entities.TypeCourse;
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
        // Replace deprecated initMocks with openMocks
        MockitoAnnotations.openMocks(this);
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
        Instructor instructor = new Instructor(1L, "John", "Doe", LocalDate.of(2022, 1, 1), new HashSet<>());
        Course course = new Course(1L, 3, TypeCourse.INDIVIDUAL, Support.SKI, 120.0f, 5);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor result = instructorService.addInstructorAndAssignToCourse(instructor, 1L);

        assertNotNull(result);
        assertEquals(1, result.getCourses().size());
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

    @Test
    void testDeleteInstructor() {
        // Simuler que l'instructeur existe avant la suppression
        when(instructorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(instructorRepository).deleteById(1L);

        instructorService.deleteInstructor(1L);

        // Vérifier que `existsById` a bien été appelé
        verify(instructorRepository, times(1)).existsById(1L);
        // Vérifier que `deleteById` a bien été appelé
        verify(instructorRepository, times(1)).deleteById(1L);
    }

    @Test
    void testRetrieveInstructorThrowsException() {
        when(instructorRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> instructorService.retrieveInstructor(1L));

        verify(instructorRepository, times(1)).findById(anyLong());
    }

    @Test
    void testDeleteInstructorNotFound() {
        Long instructorId = 99L;
        when(instructorRepository.existsById(instructorId)).thenReturn(false);
        instructorService.deleteInstructor(instructorId);
        verify(instructorRepository, times(1)).existsById(instructorId);
        verify(instructorRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteInstructorWithNullId() {
        // Test that the service gracefully handles null instructor ID
        instructorService.deleteInstructor(null);
        
        // Verify that repository methods are never called with null ID
        verify(instructorRepository, never()).existsById(any());
        verify(instructorRepository, never()).deleteById(any());
    }

    @Test
    void testAddInstructorAndAssignToCourseWithTooManyCourses() {
        Instructor instructor = new Instructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE, new HashSet<>());

        // Add 10 courses to simulate max capacity
        for (int i = 1; i <= 10; i++) {
            Course course = new Course((long) i, 3, TypeCourse.INDIVIDUAL, Support.SKI, 120.0f, 5);
            instructor.getCourses().add(course);
        }

        Course newCourse = new Course(11L, 3, TypeCourse.INDIVIDUAL, Support.SKI, 120.0f, 5);
        when(courseRepository.findById(11L)).thenReturn(Optional.of(newCourse));

        // The service should still add the course even if there are many courses
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor result = instructorService.addInstructorAndAssignToCourse(instructor, 11L);

        assertNotNull(result);
        assertEquals(11, result.getCourses().size());
        verify(courseRepository, times(1)).findById(11L);
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }

    @Test
    void testRetrieveAllInstructorsEmpty() {
        when(instructorRepository.findAll()).thenReturn(Collections.emptyList());

        List<Instructor> retrievedInstructors = instructorService.retrieveAllInstructors();

        assertNotNull(retrievedInstructors);
        assertTrue(retrievedInstructors.isEmpty());
        verify(instructorRepository, times(1)).findAll();
    }

    @Test
    void testUpdateInstructorWithMismatchedId() {
        // Try to update with instructor that has ID 2
        Instructor updatedInstructor = new Instructor(2L, INSTRUCTOR_FIRST_NAME_UPDATED, INSTRUCTOR_LAST_NAME, INSTRUCTOR_HIRE_DATE_UPDATED, new HashSet<>());

        when(instructorRepository.findById(2L)).thenReturn(Optional.empty());

        Instructor result = instructorService.updateInstructor(updatedInstructor);

        assertNull(result);
        verify(instructorRepository, times(1)).findById(2L);
        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    void testAddInstructorAndAssignToCourseNullParameters() {
        Instructor result = instructorService.addInstructorAndAssignToCourse(null, null);

        assertNull(result);
        verify(courseRepository, never()).findById(anyLong());
        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    void testUpdateInstructorInfo() {
        // Setup
        Instructor updatedInstructor = new Instructor();
        updatedInstructor.setFirstName("Updated First Name");
        updatedInstructor.setLastName("Updated Last Name");
        
        // ...existing code...
    }
}