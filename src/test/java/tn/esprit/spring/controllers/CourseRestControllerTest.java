package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICourseServices courseServices;

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
                        .content(new ObjectMapper().writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numCourse").value(course.getNumCourse()))
                .andExpect(jsonPath("$.level").value(course.getLevel()));

        verify(courseServices, times(1)).addCourse(any(Course.class));
    }

    @Test
    void testAddCourseInvalidInput() throws Exception {
        Course invalidCourse = new Course(); // Données manquantes ou invalides
        mockMvc.perform(post("/course/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidCourse)))
                .andExpect(status().isBadRequest()); // Vérifie que la validation échoue

        verify(courseServices, never()).addCourse(any(Course.class));
    }

    @Test
    void testAddCourseThrowsException() throws Exception {
        when(courseServices.addCourse(any(Course.class))).thenThrow(new RuntimeException("Erreur interne"));

        mockMvc.perform(post("/course/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(course)))
                .andExpect(status().isInternalServerError());

        verify(courseServices, times(1)).addCourse(any(Course.class));
    }

    @Test
    void testGetAllCoursesSuccess() throws Exception {
        List<Course> courses = Arrays.asList(
                createSampleCourse(1L, 2, TypeCourse.INDIVIDUAL, Support.SNOWBOARD, 100.5f, 3),
                createSampleCourse(2L, 3, TypeCourse.COLLECTIVE_ADULT, Support.SKI, 150.0f, 2)
        );
        when(courseServices.retrieveAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/course/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(courses.size()));

        verify(courseServices, times(1)).retrieveAllCourses();
    }

    @Test
    void testUpdateCourseSuccess() throws Exception {
        course.setLevel(5);
        when(courseServices.updateCourse(any(Course.class))).thenReturn(course);

        mockMvc.perform(put("/course/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(5));

        verify(courseServices, times(1)).updateCourse(any(Course.class));
    }

    @Test
    void testUpdateCourseNotFound() throws Exception {
        when(courseServices.updateCourse(any(Course.class))).thenReturn(null);

        mockMvc.perform(put("/course/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(course)))
                .andExpect(status().isNotFound());

        verify(courseServices, times(1)).updateCourse(any(Course.class));
    }

    @Test
    void testGetCourseByIdSuccess() throws Exception {
        when(courseServices.retrieveCourse(1L)).thenReturn(course);

        mockMvc.perform(get("/course/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numCourse").value(1));

        verify(courseServices, times(1)).retrieveCourse(1L);
    }

    @Test
    void testGetCourseByIdNotFound() throws Exception {
        when(courseServices.retrieveCourse(5L)).thenReturn(null);

        mockMvc.perform(get("/course/get/5"))
                .andExpect(status().isNotFound());

        verify(courseServices, times(1)).retrieveCourse(5L);
    }



    // Méthode utilitaire pour créer un objet Course
    private Course createSampleCourse(Long id, int level, TypeCourse typeCourse, Support support, float price, int timeSlot) {
        Course course = new Course();
        course.setNumCourse(id);
        course.setLevel(level);
        course.setTypeCourse(typeCourse);
        course.setSupport(support);
        course.setPrice(price);
        course.setTimeSlot(timeSlot);
        return course;
    }
}