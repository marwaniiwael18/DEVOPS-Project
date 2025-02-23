package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.entities.Instructor;

import tn.esprit.spring.services.IInstructorServices;
import tn.esprit.spring.repositories.ICourseRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstructorRestController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class InstructorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IInstructorServices instructorServices;

    private Instructor instructor;
    private static final String INSTRUCTOR_ADD_ENDPOINT = "/instructor/add";
    private static final String INSTRUCTOR_UPDATE_ENDPOINT = "/instructor/update";
    private static final String INSTRUCTOR_ADD_AND_ASSIGN_ENDPOINT = "/instructor/addAndAssignToCourse/";
    // Define constants for JSON paths
    private static final String JSON_PATH_NUM_INSTRUCTOR = "$.numInstructor";
    private static final String JSON_PATH_FIRST_NAME = "$.firstName";
    private static final String JSON_PATH_LAST_NAME = "$.lastName";


    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    void setUp() {
        // Register JavaTimeModule to handle LocalDate serialization/deserialization
        objectMapper.registerModule(new JavaTimeModule());

        // Create a sample instructor with dummy data
        instructor = createSampleInstructor(1L, "John", "Doe");
    }

    @Test
    void testAddInstructorSuccess() throws Exception {
        when(instructorServices.addInstructor(any(Instructor.class))).thenReturn(instructor);

        mockMvc.perform(post(INSTRUCTOR_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(instructor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NUM_INSTRUCTOR).value(instructor.getNumInstructor()))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME).value(instructor.getFirstName()))
                .andExpect(jsonPath(JSON_PATH_LAST_NAME).value(instructor.getLastName()));
        verify(instructorServices, times(1)).addInstructor(any(Instructor.class));
    }

    @Test
    void testAddInstructorInvalidInput() throws Exception {
        Instructor invalidInstructor = new Instructor(); // Missing or invalid data
        mockMvc.perform(post(INSTRUCTOR_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidInstructor)))
                .andExpect(status().isBadRequest()); // Expecting validation to fail

        verify(instructorServices, never()).addInstructor(any(Instructor.class));
    }

    @Test
    void testAddAndAssignInstructorToCourse() throws Exception {
        when(instructorServices.addInstructorAndAssignToCourse(any(Instructor.class), anyLong())).thenReturn(instructor);

        mockMvc.perform(put(INSTRUCTOR_ADD_AND_ASSIGN_ENDPOINT + "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(instructor)))  // Use the injected ObjectMapper
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NUM_INSTRUCTOR).value(instructor.getNumInstructor()))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME).value(instructor.getFirstName()))
                .andExpect(jsonPath(JSON_PATH_LAST_NAME).value(instructor.getLastName()));

        verify(instructorServices, times(1)).addInstructorAndAssignToCourse(any(Instructor.class), anyLong());
    }

    @Test
    void testGetAllInstructorsSuccess() throws Exception {
        List<Instructor> instructors = Arrays.asList(
                createSampleInstructor(1L, "John", "Doe"),
                createSampleInstructor(2L, "Jane", "Smith")
        );
        when(instructorServices.retrieveAllInstructors()).thenReturn(instructors);

        mockMvc.perform(get("/instructor/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(instructors.size()));

        verify(instructorServices, times(1)).retrieveAllInstructors();
    }

    @Test
    void testUpdateInstructorSuccess() throws Exception {
        instructor.setFirstName("UpdatedName");
        when(instructorServices.updateInstructor(any(Instructor.class))).thenReturn(instructor);

        mockMvc.perform(put(INSTRUCTOR_UPDATE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(instructor)))  // Use the injected ObjectMapper
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME).value("UpdatedName"));

        verify(instructorServices, times(1)).updateInstructor(any(Instructor.class));
    }


    @Test
    void testGetInstructorByIdSuccess() throws Exception {
        when(instructorServices.retrieveInstructor(1L)).thenReturn(instructor);

        mockMvc.perform(get("/instructor/get/{id-instructor}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NUM_INSTRUCTOR).value(1L));

        verify(instructorServices, times(1)).retrieveInstructor(1L);
    }

    @Test
    void testGetInstructorByIdNotFound() throws Exception {
        when(instructorServices.retrieveInstructor(5L)).thenReturn(null);

        mockMvc.perform(get("/instructor/get/{id-instructor}", 5))
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
        Instructor invalidInstructor = new Instructor();
        invalidInstructor.setFirstName(null);  // Champ obligatoire manquant
        invalidInstructor.setLastName("Doe");

        mockMvc.perform(post(INSTRUCTOR_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInstructor)))
                .andExpect(status().isBadRequest());  // Validation échouée
    }
    @Test
    void testDeleteInstructorSuccess() throws Exception {
        Long instructorId = 1L;
        doNothing().when(instructorServices).deleteInstructor(instructorId);

        mockMvc.perform(delete("/instructor/delete/{id-instructor}", instructorId))
                .andExpect(status().isNoContent());  // Vérifie que la suppression est bien faite

        verify(instructorServices, times(1)).deleteInstructor(instructorId);
    }
    @Test
    void testDeleteInstructorNotFound() throws Exception {
        Long nonExistentId = 99L;
        doNothing().when(instructorServices).deleteInstructor(nonExistentId);

        mockMvc.perform(delete("/instructor/delete/{id-instructor}", nonExistentId))
                .andExpect(status().isNoContent());  // Même si l'élément n'existe pas, la suppression est "réussie"

        verify(instructorServices, times(1)).deleteInstructor(nonExistentId);
    }
    @Test
    void testAddInstructorMissingLastName() throws Exception {
        Instructor invalidInstructor = new Instructor();
        invalidInstructor.setFirstName("John");  // LastName est manquant

        mockMvc.perform(post(INSTRUCTOR_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInstructor)))
                .andExpect(status().isBadRequest());  // Vérifie que la validation échoue
    }







    // Utility method to create a sample Instructor
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
