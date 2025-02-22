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
        // ARRANGE
        Instructor instructor = new Instructor(null, "John", "Doe", LocalDate.of(2022, 1, 1), new HashSet<>());
        Instructor savedInstructor = new Instructor(1L, "John", "Doe", LocalDate.of(2022, 1, 1), new HashSet<>());

        when(instructorRepository.save(any(Instructor.class))).thenReturn(savedInstructor);

        // ACT
        Instructor result = instructorService.addInstructor(instructor);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getNumInstructor());
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }

    @Test
    void testRetrieveAllInstructors() {
        // ARRANGE
        List<Instructor> instructors = Arrays.asList(
                new Instructor(1L, "John", "Doe", LocalDate.of(2022, 1, 1), new HashSet<>()),
                new Instructor(2L, "Jane", "Smith", LocalDate.of(2021, 5, 10), new HashSet<>())
        );

        when(instructorRepository.findAll()).thenReturn(instructors);

        // ACT
        List<Instructor> retrievedInstructors = instructorService.retrieveAllInstructors();

        // ASSERT
        assertNotNull(retrievedInstructors);
        assertEquals(2, retrievedInstructors.size());
        verify(instructorRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveInstructorFound() {
        // ARRANGE
        Instructor instructor = new Instructor(1L, "John", "Doe", LocalDate.of(2022, 1, 1), new HashSet<>());
        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));

        // ACT
        Instructor retrievedInstructor = instructorService.retrieveInstructor(1L);

        // ASSERT
        assertNotNull(retrievedInstructor);
        assertEquals(1L, retrievedInstructor.getNumInstructor());
        verify(instructorRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveInstructorNotFound() {
        // ARRANGE
        when(instructorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT
        Instructor retrievedInstructor = instructorService.retrieveInstructor(99L);

        // ASSERT
        assertNull(retrievedInstructor);
        verify(instructorRepository, times(1)).findById(99L);
    }

    @Test
    void testUpdateInstructor() {
        // ARRANGE
        Instructor existingInstructor = new Instructor(1L, "John", "Doe", LocalDate.of(2022, 1, 1), new HashSet<>());
        Instructor updatedInstructor = new Instructor(1L, "Johnny", "Doe", LocalDate.of(2023, 2, 15), new HashSet<>());

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(existingInstructor));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(updatedInstructor);

        // ACT
        Instructor result = instructorService.updateInstructor(updatedInstructor);

        // ASSERT
        assertNotNull(result);
        assertEquals("Johnny", result.getFirstName());
        verify(instructorRepository, times(1)).findById(1L);  // 🔥 Vérifie bien que findById a été appelé
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }


    @Test
    void testAddInstructorAndAssignToCourse() {
        // ARRANGE
        Instructor instructor = new Instructor(null, "John", "Doe", LocalDate.of(2022, 1, 1), new HashSet<>());
        Course course = new Course(1L, 3, null, null, 120.0f, 5, null);
        Instructor savedInstructor = new Instructor(1L, "John", "Doe", LocalDate.of(2022, 1, 1), new HashSet<>(Collections.singleton(course)));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(savedInstructor);

        // ACT
        Instructor result = instructorService.addInstructorAndAssignToCourse(instructor, 1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.getCourses().size());
        verify(courseRepository, times(1)).findById(1L);
        verify(instructorRepository, times(1)).save(any(Instructor.class));
    }

    @Test
    void testAddInstructorAndAssignToCourseNotFound() {
        // ARRANGE
        Instructor instructor = new Instructor(1L, "John", "Doe", LocalDate.of(2022, 1, 1), new HashSet<>());
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());  // 🔥 Simule un cours introuvable

        // ACT
        Instructor result = instructorService.addInstructorAndAssignToCourse(instructor, 99L);

        // ASSERT
        assertNull(result);  // 🔥 On s'attend maintenant à `null`
        verify(courseRepository, times(1)).findById(99L);
        verify(instructorRepository, never()).save(any(Instructor.class));  // 🔥 Vérifie que `save()` n'est PAS appelé
    }

}
