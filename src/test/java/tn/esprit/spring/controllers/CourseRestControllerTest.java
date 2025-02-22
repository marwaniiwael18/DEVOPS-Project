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
    @Autowired
    private ObjectMapper objectMapper; // Ajout de ObjectMapper

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
    @Test
    void testGetCourseByIdSuccess() throws Exception {
        logger.info("Test: Getting a course by ID successfully");
        Long courseId = 1L;

        when(courseServices.retrieveCourse(courseId)).thenReturn(course);

        mockMvc.perform(get("/course/get/{id-course}", courseId)  // ← Corrige ici !
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numCourse").value(course.getNumCourse()));

        verify(courseServices, times(1)).retrieveCourse(courseId);
    }
    @Test
    void testHandleRuntimeException() throws Exception {
        when(courseServices.retrieveCourse(anyLong())).thenThrow(new RuntimeException("Internal error"));

        mockMvc.perform(get("/course/get/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal error"));
    }


    @Test
    void testGetAllCoursesSuccess() throws Exception {
        logger.info("Test: Getting all courses successfully");
        List<Course> courses = Arrays.asList(course, createSampleCourse(2L, 3, TypeCourse.INDIVIDUAL, Support.SKI, 150.0f, 2));

        when(courseServices.retrieveAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/course/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(courses.size()));

        verify(courseServices, times(1)).retrieveAllCourses();
    }
    @Test
    void testGetCourseByIdNotFound() throws Exception {
        Long nonExistentId = 99L;

        when(courseServices.retrieveCourse(nonExistentId)).thenReturn(null);  // Simule l'absence du cours

        mockMvc.perform(get("/course/get/{id-course}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());  // On attend un 404
    }
    @Test
    void testGetCourseByIdInvalidFormat() throws Exception {
        mockMvc.perform(get("/course/get/invalidId")  // ID invalide
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());  // On attend un 400 BAD REQUEST
    }

    @Test
    void testUpdateCourseSuccess() throws Exception {
        logger.info("Test: Updating a course successfully");
        course.setPrice(200.0f);
        when(courseServices.updateCourse(any(Course.class))).thenReturn(course);

        mockMvc.perform(put("/course/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(course.getPrice()));

        verify(courseServices, times(1)).updateCourse(any(Course.class));
    }
    @Test
    void testUpdateCourseNotFound() throws Exception {
        Course updatedCourse = createSampleCourse(2L, 3, TypeCourse.INDIVIDUAL, Support.SKI, 120.0f, 5);

        when(courseServices.updateCourse(any(Course.class))).thenReturn(null);  // Simule un cours non trouvé

        mockMvc.perform(put("/course/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCourse)))
                .andExpect(status().isNotFound());  // On attend un 404
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
