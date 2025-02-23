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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServicesImplTest {

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRetrieveAllCourses() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(new Course(1L, 2, TypeCourse.INDIVIDUAL, Support.SKI, 120.0f, 5)));

        List<Course> retrievedCourses = courseService.retrieveAllCourses();

        assertNotNull(retrievedCourses);
        assertEquals(1, retrievedCourses.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveCourseFound() {
        Course course = new Course(1L, 3, TypeCourse.INDIVIDUAL, Support.SKI, 120.0f, 5);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course retrievedCourse = courseService.retrieveCourse(1L);

        assertNotNull(retrievedCourse);
        assertEquals(1L, retrievedCourse.getNumCourse());
        verify(courseRepository, times(1)).findById(1L);
    }


    @Test
    void testRetrieveCourseNotFound() {
        // ARRANGE
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT
        Course retrievedCourse = courseService.retrieveCourse(99L);

        // ASSERT
        assertNull(retrievedCourse);
        verify(courseRepository, times(1)).findById(99L);
    }

    @Test
    void testAddCourse() {
        // ARRANGE
        Course newCourse = new Course(null, 3, TypeCourse.INDIVIDUAL, Support.SKI, 120.0f, 5, null);
        Course savedCourse = new Course(1L, 3, TypeCourse.INDIVIDUAL, Support.SKI, 120.0f, 5, null);
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // ACT
        Course result = courseService.addCourse(newCourse);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getNumCourse());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testUpdateCourse() {
        // ARRANGE
        Course existingCourse = new Course(1L, 3, TypeCourse.INDIVIDUAL, Support.SKI, 120.0f, 5, null);
        Course updatedCourse = new Course(1L, 5, TypeCourse.COLLECTIVE_ADULT, Support.SNOWBOARD, 130.0f, 6, null);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        // ACT
        Course result = courseService.updateCourse(updatedCourse);

        // ASSERT
        assertNotNull(result);
        assertEquals(5, result.getLevel());
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testUpdateCourseNotFound() {
        // ARRANGE
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        Course updatedCourse = new Course(99L, 4, TypeCourse.COLLECTIVE_ADULT, Support.SNOWBOARD, 130.0f, 6, null);

        // ACT
        Course result = courseService.updateCourse(updatedCourse);

        // ASSERT
        assertNull(result);
        verify(courseRepository, times(1)).findById(99L);
        verify(courseRepository, never()).save(any(Course.class));
    }
    @Test
    void testDeleteCourseSuccess() {
        // ARRANGE
        Long courseId = 1L;
        when(courseRepository.existsById(courseId)).thenReturn(true);
        doNothing().when(courseRepository).deleteById(courseId);

        // ACT
        courseService.deleteCourse(courseId);

        // ASSERT
        verify(courseRepository, times(1)).existsById(courseId);
        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    void testDeleteCourseNotFound() {
        // ARRANGE
        Long courseId = 99L;
        when(courseRepository.existsById(courseId)).thenReturn(false);

        // ACT
        courseService.deleteCourse(courseId);

        // ASSERT
        verify(courseRepository, times(1)).existsById(courseId);
        verify(courseRepository, never()).deleteById(anyLong());
    }

}
