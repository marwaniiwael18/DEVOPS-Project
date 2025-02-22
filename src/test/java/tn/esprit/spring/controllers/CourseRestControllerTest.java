package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.services.ICourseServices;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseRestController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class CourseRestControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(CourseRestControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICourseServices courseServices;

    private Course course;
    private static final String COURSE_ADD_ENDPOINT = "/course/add";

    @BeforeEach
    void setUp() {
        course = createSampleCourse(1L, 2, TypeCourse.INDIVIDUAL, Support.SNOWBOARD, 100.5f, 3);
    }

    @Test
    void testAddCourseSuccess() throws Exception {
        logger.info("Test: Adding a course successfully");
        when(courseServices.addCourse(any(Course.class))).thenReturn(course);

        mockMvc.perform(post(COURSE_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numCourse").value(course.getNumCourse()));

        verify(courseServices, times(1)).addCourse(any(Course.class));
    }

    private Course createSampleCourse(Long id, int level, TypeCourse typeCourse, Support support, float price, int timeSlot) {
        Course newCourse = new Course();
        newCourse.setNumCourse(id);
        newCourse.setLevel(level);
        newCourse.setTypeCourse(typeCourse);
        newCourse.setSupport(support);
        newCourse.setPrice(price);
        newCourse.setTimeSlot(timeSlot);
        return newCourse;
    }
}
