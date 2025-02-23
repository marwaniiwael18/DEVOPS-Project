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
import static org.mockito.ArgumentMatchers.anyLong;
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
    private ObjectMapper objectMapper;

    private Course course;

    @BeforeEach
    void setUp() {
        course = createSampleCourse(1L, 2, TypeCourse.INDIVIDUAL, Support.SNOWBOARD, 100.5f, 3);
    }

    @Test
    void testAddCourseSuccess() throws Exception {
        when(courseServices.addCourse(any(Course.class))).thenReturn(course);

        mockMvc.perform(post("/course/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numCourse").value(course.getNumCourse()));

        verify(courseServices, times(1)).addCourse(any(Course.class));
    }

    @Test
    void testGetCourseByIdSuccess() throws Exception {
        Long courseId = 1L;
        when(courseServices.retrieveCourse(courseId)).thenReturn(course);

        mockMvc.perform(get("/course/get/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numCourse").value(course.getNumCourse()));

        verify(courseServices, times(1)).retrieveCourse(courseId);
    }

    @Test
    void testGetCourseByIdNotFound() throws Exception {
        Long nonExistentId = 99L;
        when(courseServices.retrieveCourse(nonExistentId)).thenReturn(null);

        mockMvc.perform(get("/course/get/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(courseServices, times(1)).retrieveCourse(nonExistentId);
    }

    @Test
    void testGetCourseByIdInvalidFormat() throws Exception {
        mockMvc.perform(get("/course/get/invalidId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllCoursesSuccess() throws Exception {
        List<Course> courses = Arrays.asList(course, createSampleCourse(2L, 3, TypeCourse.INDIVIDUAL, Support.SKI, 150.0f, 2));
        when(courseServices.retrieveAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/course/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(courses.size()));

        verify(courseServices, times(1)).retrieveAllCourses();
    }

    @Test
    void testUpdateCourseSuccess() throws Exception {
        course.setPrice(200.0f);
        when(courseServices.updateCourse(any(Course.class))).thenReturn(course);

        mockMvc.perform(put("/course/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(course.getPrice()));

        verify(courseServices, times(1)).updateCourse(any(Course.class));
    }

    @Test
    void testUpdateCourseNotFound() throws Exception {
        Course updatedCourse = createSampleCourse(2L, 3, TypeCourse.INDIVIDUAL, Support.SKI, 120.0f, 5);
        when(courseServices.updateCourse(any(Course.class))).thenReturn(null);

        mockMvc.perform(put("/course/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCourse)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCourseSuccess() throws Exception {
        Long courseId = 1L;
        when(courseServices.retrieveCourse(courseId)).thenReturn(course);
        doNothing().when(courseServices).deleteCourse(courseId);

        mockMvc.perform(delete("/course/delete/{id}", courseId))
                .andExpect(status().isNoContent()); // Vérifie 204 au lieu de 200


        verify(courseServices, times(1)).retrieveCourse(courseId);
        verify(courseServices, times(1)).deleteCourse(courseId);
    }

    @Test
    void testDeleteCourseNotFound() throws Exception {
        Long nonExistentId = 99L;
        when(courseServices.retrieveCourse(nonExistentId)).thenReturn(null);

        mockMvc.perform(delete("/course/delete/{id}", nonExistentId))
                .andExpect(status().isNotFound());
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
