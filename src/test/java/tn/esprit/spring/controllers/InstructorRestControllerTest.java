package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.config.TestConfig;
import tn.esprit.spring.dto.InstructorDTO;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.mappers.InstructorMapper;
import tn.esprit.spring.services.IInstructorServices;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstructorRestController.class)
@Import(TestConfig.class)
public class InstructorRestControllerTest {

    // API endpoints constants
    private static final String INSTRUCTOR_ADD_ENDPOINT = "/instructor/add";
    private static final String INSTRUCTOR_UPDATE_ENDPOINT = "/instructor/update";
    private static final String INSTRUCTOR_ADD_AND_ASSIGN_ENDPOINT = "/instructor/addAndAssignToCourse/";
    private static final String INSTRUCTOR_ALL_ENDPOINT = "/instructor/all";
    private static final String INSTRUCTOR_GET_ENDPOINT = "/instructor/get/{id-instructor}";
    private static final String INSTRUCTOR_DELETE_ENDPOINT = "/instructor/delete/{id-instructor}";

    // JSON path constants
    private static final String JSON_PATH_ID = "$.id";
    private static final String JSON_PATH_FIRST_NAME = "$.firstName";
    private static final String JSON_PATH_LAST_NAME = "$.lastName";
    private static final String JSON_PATH_SIZE = "$.size()";

    // Test data constants
    private static final String INSTRUCTOR_FIRST_NAME = "John";
    private static final String INSTRUCTOR_LAST_NAME = "Doe";
    private static final String UPDATED_FIRST_NAME = "UpdatedName";
    private static final String JANE = "Jane";
    private static final String SMITH = "Smith";

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private IInstructorServices instructorServices;
    
    @Autowired
    private InstructorMapper instructorMapper;
    
    @Autowired
    private ObjectMapper objectMapper;

    private Instructor instructor;
    private InstructorDTO instructorDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        instructor = createSampleInstructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME);
        instructorDTO = instructorMapper.toDTO(instructor);
    }

    @Test
    void testAddInstructorSuccess() throws Exception {
        when(instructorServices.addInstructor(any(Instructor.class))).thenReturn(instructor);

        mockMvc.perform(post(INSTRUCTOR_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(instructorDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(instructor.getNumInstructor()))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME).value(instructor.getFirstName()))
                .andExpect(jsonPath(JSON_PATH_LAST_NAME).value(instructor.getLastName()));
        verify(instructorServices, times(1)).addInstructor(any(Instructor.class));
    }

    @Test
    void testAddInstructorInvalidInput() throws Exception {
        InstructorDTO invalidInstructorDTO = new InstructorDTO(); // Missing or invalid data
        mockMvc.perform(post(INSTRUCTOR_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInstructorDTO)))
                .andExpect(status().isBadRequest()); // Expecting validation to fail

        verify(instructorServices, never()).addInstructor(any(Instructor.class));
    }

    @Test
    void testAddAndAssignInstructorToCourse() throws Exception {
        when(instructorServices.addInstructorAndAssignToCourse(any(Instructor.class), anyLong())).thenReturn(instructor);

        mockMvc.perform(put(INSTRUCTOR_ADD_AND_ASSIGN_ENDPOINT + "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(instructorDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(instructor.getNumInstructor()))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME).value(instructor.getFirstName()))
                .andExpect(jsonPath(JSON_PATH_LAST_NAME).value(instructor.getLastName()));

        verify(instructorServices, times(1)).addInstructorAndAssignToCourse(any(Instructor.class), anyLong());
    }

    @Test
    void testGetAllInstructorsSuccess() throws Exception {
        List<Instructor> instructors = Arrays.asList(
                createSampleInstructor(1L, INSTRUCTOR_FIRST_NAME, INSTRUCTOR_LAST_NAME),
                createSampleInstructor(2L, JANE, SMITH)
        );
        when(instructorServices.retrieveAllInstructors()).thenReturn(instructors);

        mockMvc.perform(get(INSTRUCTOR_ALL_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_SIZE).value(instructors.size()));

        verify(instructorServices, times(1)).retrieveAllInstructors();
    }

    @Test
    void testUpdateInstructorSuccess() throws Exception {
        instructor.setFirstName(UPDATED_FIRST_NAME);
        instructorDTO = instructorMapper.toDTO(instructor);
        when(instructorServices.updateInstructor(any(Instructor.class))).thenReturn(instructor);

        mockMvc.perform(put(INSTRUCTOR_UPDATE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(instructorDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME).value(UPDATED_FIRST_NAME));

        verify(instructorServices, times(1)).updateInstructor(any(Instructor.class));
    }

    @Test
    void testUpdateInstructorNotFound() throws Exception {
        instructor.setFirstName(UPDATED_FIRST_NAME);
        instructorDTO = instructorMapper.toDTO(instructor);
        when(instructorServices.updateInstructor(any(Instructor.class))).thenReturn(null);

        mockMvc.perform(put(INSTRUCTOR_UPDATE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(instructorDTO)))
                .andExpect(status().isNotFound());

        verify(instructorServices, times(1)).updateInstructor(any(Instructor.class));
    }

    @Test
    void testAddAndAssignInstructorToCourseNotFound() throws Exception {
        when(instructorServices.addInstructorAndAssignToCourse(any(Instructor.class), anyLong())).thenReturn(null);

        mockMvc.perform(put(INSTRUCTOR_ADD_AND_ASSIGN_ENDPOINT + "99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(instructorDTO)))
                .andExpect(status().isNotFound());

        verify(instructorServices, times(1)).addInstructorAndAssignToCourse(any(Instructor.class), anyLong());
    }

    @Test
    void testAddInstructorNullInstructor() throws Exception {
        when(instructorServices.addInstructor(null)).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(post(INSTRUCTOR_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddAndAssignInstructorToCourseInvalidCourseId() throws Exception {
        mockMvc.perform(put(INSTRUCTOR_ADD_AND_ASSIGN_ENDPOINT + "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(instructorDTO)))
                .andExpect(status().isBadRequest());

        verify(instructorServices, never()).addInstructorAndAssignToCourse(any(Instructor.class), anyLong());
    }

    @Test
    void testDeleteInstructorInvalidId() throws Exception {
        mockMvc.perform(delete("/instructor/delete/invalid"))
                .andExpect(status().isBadRequest());

        verify(instructorServices, never()).deleteInstructor(anyLong());
    }

    @Test
    void testGetInstructorByIdSuccess() throws Exception {
        when(instructorServices.retrieveInstructor(1L)).thenReturn(instructor);

        mockMvc.perform(get(INSTRUCTOR_GET_ENDPOINT, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(1L));

        verify(instructorServices, times(1)).retrieveInstructor(1L);
    }

    @Test
    void testGetInstructorByIdNotFound() throws Exception {
        when(instructorServices.retrieveInstructor(5L)).thenReturn(null);

        mockMvc.perform(get(INSTRUCTOR_GET_ENDPOINT, 5))
                .andExpect(status().isNotFound());

        verify(instructorServices, times(1)).retrieveInstructor(5L);
    }

    @Test
    void testGetInstructorByIdInvalidId() throws Exception {
        mockMvc.perform(get("/instructor/get/invalid"))
                .andExpect(status().isBadRequest());

        verify(instructorServices, never()).retrieveInstructor(anyLong());
    }

    @Test
    void testAddInstructorInvalidFirstName() throws Exception {
        InstructorDTO invalidInstructorDTO = new InstructorDTO();
        invalidInstructorDTO.setFirstName(null);
        invalidInstructorDTO.setLastName(INSTRUCTOR_LAST_NAME);

        mockMvc.perform(post(INSTRUCTOR_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInstructorDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteInstructorSuccess() throws Exception {
        Long instructorId = 1L;
        doNothing().when(instructorServices).deleteInstructor(instructorId);

        mockMvc.perform(delete(INSTRUCTOR_DELETE_ENDPOINT, instructorId))
                .andExpect(status().isNoContent());

        verify(instructorServices, times(1)).deleteInstructor(instructorId);
    }

    @Test
    void testDeleteInstructorNotFound() throws Exception {
        Long nonExistentId = 99L;
        doNothing().when(instructorServices).deleteInstructor(nonExistentId);

        mockMvc.perform(delete(INSTRUCTOR_DELETE_ENDPOINT, nonExistentId))
                .andExpect(status().isNoContent());

        verify(instructorServices, times(1)).deleteInstructor(nonExistentId);
    }

    @Test
    void testAddInstructorMissingLastName() throws Exception {
        InstructorDTO invalidInstructorDTO = new InstructorDTO();
        invalidInstructorDTO.setFirstName(INSTRUCTOR_FIRST_NAME);

        mockMvc.perform(post(INSTRUCTOR_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInstructorDTO)))
                .andExpect(status().isBadRequest());
    }

    private Instructor createSampleInstructor(Long id, String firstName, String lastName) {
        Instructor newInstructor = new Instructor();
        newInstructor.setNumInstructor(id);
        newInstructor.setFirstName(firstName);
        newInstructor.setLastName(lastName);
        newInstructor.setDateOfHire(LocalDate.now());
        newInstructor.setCourses(new HashSet<>());
        return newInstructor;
    }
}
