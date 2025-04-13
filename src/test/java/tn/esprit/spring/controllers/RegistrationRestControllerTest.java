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

    private static final String API_REGISTRATION_ASSIGN_COURSE = "/registration/assignToCourse/{numRegistration}/{numCourse}";
    private static final String API_REGISTRATION_NUM_WEEKS = "/registration/numWeeks/{numInstructor}/{support}";
    
    private static final String JSON_PATH_ID = "$.id";
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private IRegistrationServices registrationServices;
    

    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Registration registration;
    
    @BeforeEach
    void setUp() {
        registration = new Registration();
        registration.setNumRegistration(1L);
        registration.setNumWeek(10);
    }

    @Test
    void testAddAndAssignToSkier() throws Exception {
        // Create a valid DTO
        RegistrationDTO registrationDTO = createSampleRegistrationDTO();
        Registration registration = new Registration();
        registration.setNumRegistration(1L);
        registration.setNumWeek(10);
        
        // Mock service methods correctly
        when(registrationServices.addRegistrationAndAssignToSkier(any(), eq(1L))).thenReturn(registration);
        
        // Perform the test
        mockMvc.perform(post("/registration/addAndAssignToSkier/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk());
        
        // Verify service was called
        verify(registrationServices).addRegistrationAndAssignToSkier(any(), eq(1L));
    }

    @Test
    void testAddAndAssignToSkierAndCourse() throws Exception {
        // Create a valid DTO
        RegistrationDTO registrationDTO = createSampleRegistrationDTO();
        Registration registration = new Registration();
        registration.setNumRegistration(1L);
        registration.setNumWeek(10);
        
        // Mock service methods correctly
        when(registrationServices.addRegistrationAndAssignToSkierAndCourse(any(), eq(1L), eq(2L))).thenReturn(registration);
        
        // Perform the test
        mockMvc.perform(post("/registration/addAndAssignToSkierAndCourse/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk());
        
        // Verify service was called
        verify(registrationServices).addRegistrationAndAssignToSkierAndCourse(any(), eq(1L), eq(2L));
    }

    @Test
    void testAssignToCourse() throws Exception {
        when(registrationServices.assignRegistrationToCourse(anyLong(), anyLong()))
                .thenReturn(registration);

        mockMvc.perform(put(API_REGISTRATION_ASSIGN_COURSE, 1, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(1L));
    }

    @Test
    void testNumWeeksCourseOfInstructorBySupport() throws Exception {
        List<Integer> mockWeeks = Arrays.asList(1, 2, 3);

        when(registrationServices.numWeeksCourseOfInstructorBySupport(anyLong(), any(Support.class)))
                .thenReturn(mockWeeks);

        mockMvc.perform(get(API_REGISTRATION_NUM_WEEKS, 1, "SNOWBOARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2))
                .andExpect(jsonPath("$[2]").value(3));
    }

    private RegistrationDTO createSampleRegistrationDTO() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(1L);
        dto.setNumWeek(10);
        return dto;
    }
}
