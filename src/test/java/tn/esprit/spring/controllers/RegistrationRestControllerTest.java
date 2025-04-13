package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.config.TestConfig;
import tn.esprit.spring.dto.RegistrationDTO;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.mappers.RegistrationMapper;
import tn.esprit.spring.services.IRegistrationServices;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationRestController.class)
@Import(TestConfig.class)
public class RegistrationRestControllerTest {

    // API endpoint constants
    private static final String API_REGISTRATION_ADD_SKIER = "/registration/addAndAssignToSkier/{skierId}";
    private static final String API_REGISTRATION_ASSIGN_COURSE = "/registration/assignToCourse/{numRegistration}/{numCourse}";
    private static final String API_REGISTRATION_ADD_SKIER_COURSE = "/registration/addAndAssignToSkierAndCourse/{numSkier}/{numCourse}";
    private static final String API_REGISTRATION_NUM_WEEKS = "/registration/numWeeks/{numInstructor}/{support}";
    
    // JSON path constants
    // Fixed: The correct JSON path for registration ID is "$.id" based on API response format
    private static final String JSON_PATH_ID = "$.id";
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private IRegistrationServices registrationServices;
    
    @Autowired
    private RegistrationMapper registrationMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Registration registration;
    private RegistrationDTO registrationDTO;
    
    @BeforeEach
    void setUp() {
        registration = new Registration();
        registration.setNumRegistration(1L);
        registration.setNumWeek(10);
        
        // Use the registrationMapper to convert to DTO
        registrationDTO = registrationMapper.toDTO(registration);
    }

    @Test
    void testAddAndAssignToSkier() throws Exception {
        when(registrationServices.addRegistrationAndAssignToSkier(any(Registration.class), anyLong()))
            .thenReturn(registration);

        mockMvc.perform(put(API_REGISTRATION_ADD_SKIER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(registration.getNumRegistration()));
                
        verify(registrationServices, times(1)).addRegistrationAndAssignToSkier(any(Registration.class), anyLong());
    }

    @Test
    public void testAssignToCourse() throws Exception {
        when(registrationServices.assignRegistrationToCourse(anyLong(), anyLong()))
                .thenReturn(registration);

        mockMvc.perform(put(API_REGISTRATION_ASSIGN_COURSE, 1, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(1L));
    }

    @Test
    public void testAddAndAssignToSkierAndCourse() throws Exception {
        when(registrationServices.addRegistrationAndAssignToSkierAndCourse(any(Registration.class), anyLong(), anyLong()))
                .thenReturn(registration);

        // Use the registrationDTO object and objectMapper instead of a hardcoded JSON string
        mockMvc.perform(put(API_REGISTRATION_ADD_SKIER_COURSE, 1, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(1L));
                
        verify(registrationServices, times(1)).addRegistrationAndAssignToSkierAndCourse(any(Registration.class), anyLong(), anyLong());
    }

    @Test
    public void testNumWeeksCourseOfInstructorBySupport() throws Exception {
        List<Integer> mockWeeks = Arrays.asList(1, 2, 3);

        when(registrationServices.numWeeksCourseOfInstructorBySupport(anyLong(), any(Support.class)))
                .thenReturn(mockWeeks);

        mockMvc.perform(get(API_REGISTRATION_NUM_WEEKS, 1, "SNOWBOARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2))
                .andExpect(jsonPath("$[2]").value(3));
    }
}
