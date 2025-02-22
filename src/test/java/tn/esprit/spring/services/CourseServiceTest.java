package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.repositories.ICourseRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseService;

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceTest.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCourse() {
        logger.info(() -> "Test: Adding a new course");

        Course course = new Course();
        course.setNumCourse(1L);
        course.setLevel(2);

        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course savedCourse = courseService.addCourse(course);

        assertNotNull(savedCourse);
        assertEquals(1L, savedCourse.getNumCourse());
        verify(courseRepository, times(1)).save(any(Course.class));
    }
}
