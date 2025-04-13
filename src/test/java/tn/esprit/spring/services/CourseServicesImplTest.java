package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.repositories.ICourseRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServicesImplTest {

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseService;

    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        course = new Course(1L, 3, TypeCourse.COLLECTIVE_ADULT, Support.SKI, 120.0f, 5, null);
    }

    @Test
    void testRetrieveAllCourses() {
        // Setup
        List<Course> courses = Arrays.asList(
                new Course(1L, 3, TypeCourse.COLLECTIVE_ADULT, Support.SKI, 120.0f, 5, null),
                new Course(2L, 4, TypeCourse.COLLECTIVE_CHILDREN, Support.SNOWBOARD, 100.0f, 6, null)
        );
        when(courseRepository.findAll()).thenReturn(courses);

        // Execute
        List<Course> result = courseService.retrieveAllCourses();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAllCoursesEmpty() {
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());
        
        List<Course> result = courseService.retrieveAllCourses();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveCourseFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course retrievedCourse = courseService.retrieveCourse(1L);

        assertNotNull(retrievedCourse);
        assertEquals(1L, retrievedCourse.getNumCourse());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveCourseNotFound() {
        // Setup
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execute
        Course result = courseService.retrieveCourse(99L);

        // Verify
        assertNull(result);
        verify(courseRepository, times(1)).findById(99L);
    }

    @Test
    void testRetrieveCourseWithNullId() {
        Course result = courseService.retrieveCourse(null);
        
        assertNull(result);
        verify(courseRepository, never()).findById(anyLong());
    }

    @Test
    void testAddCourse() {
        // Setup
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Execute
        Course result = courseService.addCourse(course);

        // Verify
        assertNotNull(result);
        assertEquals(1L, result.getNumCourse());
        assertEquals(TypeCourse.COLLECTIVE_ADULT, result.getTypeCourse());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testAddCourseWithNullInput() {
        when(courseRepository.save(null)).thenThrow(IllegalArgumentException.class);
        
        assertThrows(IllegalArgumentException.class, () -> courseService.addCourse(null));
    }

    @Test
    void testAddCourseWithAllFields() {
        Course fullCourse = new Course();
        fullCourse.setNumCourse(1L);
        fullCourse.setLevel(3);
        fullCourse.setTypeCourse(TypeCourse.COLLECTIVE_ADULT);
        fullCourse.setSupport(Support.SKI);
        fullCourse.setPrice(150.0f);
        fullCourse.setTimeSlot(5);
        
        when(courseRepository.save(any(Course.class))).thenReturn(fullCourse);
        
        Course result = courseService.addCourse(fullCourse);
        
        assertNotNull(result);
        assertEquals(1L, result.getNumCourse());
        assertEquals(3, result.getLevel());
        assertEquals(TypeCourse.COLLECTIVE_ADULT, result.getTypeCourse());
        assertEquals(Support.SKI, result.getSupport());
        assertEquals(150.0f, result.getPrice());
        assertEquals(5, result.getTimeSlot());
        verify(courseRepository, times(1)).save(fullCourse);
    }

    @Test
    void testUpdateCourse() {
        // Setup
        Course updatedCourse = new Course(1L, 5, TypeCourse.INDIVIDUAL, Support.SNOWBOARD, 150.0f, 8, null);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        // Execute
        Course result = courseService.updateCourse(updatedCourse);

        // Verify
        assertNotNull(result);
        assertEquals(TypeCourse.INDIVIDUAL, result.getTypeCourse());
        assertEquals(5, result.getLevel());
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(updatedCourse);
    }

    @Test
    void testUpdateCourseNotFound() {
        // Setup
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execute
        Course result = courseService.updateCourse(course);

        // Verify
        assertNull(result);
        verify(courseRepository, times(1)).findById(anyLong());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testUpdateCourseWithNullInput() {
        Course result = courseService.updateCourse(null);
        
        assertNull(result);
        verify(courseRepository, never()).findById(anyLong());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testDeleteCourseSuccess() {
        // Setup
        when(courseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(courseRepository).deleteById(1L);

        // Execute
        courseService.deleteCourse(1L);

        // Verify
        verify(courseRepository, times(1)).existsById(1L);
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCourseNotFound() {
        // Setup
        when(courseRepository.existsById(99L)).thenReturn(false);

        // Execute
        courseService.deleteCourse(99L);

        // Verify
        verify(courseRepository, times(1)).existsById(99L);
        verify(courseRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteCourseWithNullId() {
        courseService.deleteCourse(null);
        
        verify(courseRepository, never()).existsById(anyLong());
        verify(courseRepository, never()).deleteById(anyLong());
    }
}
