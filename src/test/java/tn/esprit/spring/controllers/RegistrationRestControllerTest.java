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

    private static final String API_REGISTRATION_ASSIGN_COURSE = "/registration/assignToCourse/{numRegistration}/{numCourse}";
    private static final String API_REGISTRATION_NUM_WEEKS = "/registration/numWeeks/{numInstructor}/{support}";
    
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private IRegistrationServices registrationServices;

    @MockBean
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
        registrationDTO = new RegistrationDTO(1L, 10, 1L, 1L, 1L);
    }

    @Test
    void testAddAndAssignToSkier() throws Exception {
        // Create a valid DTO
        RegistrationDTO validRegistrationDTO = createSampleRegistrationDTO(); // Renamed to avoid shadowing
        Registration returnedRegistration = new Registration();
        returnedRegistration.setNumRegistration(1L);
        returnedRegistration.setNumWeek(10);
        
        // Mock service methods correctly
        when(registrationServices.addRegistrationAndAssignToSkier(any(), eq(1L))).thenReturn(returnedRegistration);
        
        // Perform the test
        mockMvc.perform(post("/registration/addAndAssignToSkier/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDTO)))
                .andExpect(status().isOk());
        
        // Verify service was called
        verify(registrationServices).addRegistrationAndAssignToSkier(any(), eq(1L));
    }

    @Test
    void testAddAndAssignToSkierAndCourse() throws Exception {
        // Create a valid DTO
        RegistrationDTO validRegistrationDTO = createSampleRegistrationDTO(); // Renamed to avoid shadowing
        Registration createdRegistration = new Registration();
        createdRegistration.setNumRegistration(1L);
        createdRegistration.setNumWeek(10);
        
        // Mock service methods correctly
        when(registrationServices.addRegistrationAndAssignToSkierAndCourse(any(), eq(1L), eq(2L))).thenReturn(createdRegistration);
        
        // Perform the test
        mockMvc.perform(post("/registration/addAndAssignToSkierAndCourse/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDTO)))
                .andExpect(status().isOk());
        
        // Verify service was called
        verify(registrationServices).addRegistrationAndAssignToSkierAndCourse(any(), eq(1L), eq(2L));
    }

    @Test
    void testAddAndAssignToSkierSuccess() throws Exception {
        when(registrationMapper.toEntity(any(RegistrationDTO.class))).thenReturn(registration);
        when(registrationServices.addRegistrationAndAssignToSkier(any(Registration.class), anyLong())).thenReturn(registration);
        when(registrationMapper.toDTO(any(Registration.class))).thenReturn(registrationDTO);

        mockMvc.perform(post("/registration/addAndAssignToSkier/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numRegistration").value(1L));

        verify(registrationServices, times(1)).addRegistrationAndAssignToSkier(any(Registration.class), anyLong());
    }

    @Test
    void testAddAndAssignToSkierNotFound() throws Exception {
        when(registrationMapper.toEntity(any(RegistrationDTO.class))).thenReturn(registration);
        when(registrationServices.addRegistrationAndAssignToSkier(any(Registration.class), anyLong())).thenReturn(null);

        mockMvc.perform(post("/registration/addAndAssignToSkier/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isNotFound());

        verify(registrationServices, times(1)).addRegistrationAndAssignToSkier(any(Registration.class), anyLong());
    }

    @Test
    void testAssignToCourse() throws Exception {
        when(registrationServices.assignRegistrationToCourse(anyLong(), anyLong())).thenReturn(registration);
        when(registrationMapper.toDTO(any(Registration.class))).thenReturn(registrationDTO);

        mockMvc.perform(put("/registration/assignToCourse/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numRegistration").value(1L)); // Correct JSON path

        verify(registrationServices, times(1)).assignRegistrationToCourse(anyLong(), anyLong());
    }

    @Test
    void testAssignToCourse_NotFound() throws Exception {
        when(registrationServices.assignRegistrationToCourse(anyLong(), anyLong()))
                .thenReturn(null);

        mockMvc.perform(put(API_REGISTRATION_ASSIGN_COURSE, 1, 2))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAssignToCourse_BadRequest() throws Exception {
        when(registrationServices.assignRegistrationToCourse(anyLong(), anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        mockMvc.perform(put(API_REGISTRATION_ASSIGN_COURSE, 1, 2))
                .andExpect(status().isBadRequest()); // Expect 400 for bad input
    }

    @Test
    void testAssignToCourseWithInvalidIds() throws Exception {
        when(registrationServices.assignRegistrationToCourse(anyLong(), anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid IDs"));

        mockMvc.perform(put("/registration/assignToCourse/invalid/invalid"))
                .andExpect(status().isBadRequest()); // Expect 400 for invalid IDs

        verify(registrationServices, never()).assignRegistrationToCourse(anyLong(), anyLong());
    }

    @Test
    void testAssignToCourseSuccess() throws Exception {
        when(registrationServices.assignRegistrationToCourse(anyLong(), anyLong())).thenReturn(registration);
        when(registrationMapper.toDTO(any(Registration.class))).thenReturn(registrationDTO);

        mockMvc.perform(put("/registration/assignToCourse/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numRegistration").value(1L));

        verify(registrationServices, times(1)).assignRegistrationToCourse(anyLong(), anyLong());
    }

    @Test
    void testAssignToCourseNotFound() throws Exception {
        when(registrationServices.assignRegistrationToCourse(anyLong(), anyLong())).thenReturn(null);

        mockMvc.perform(put("/registration/assignToCourse/1/2"))
                .andExpect(status().isNotFound());

        verify(registrationServices, times(1)).assignRegistrationToCourse(anyLong(), anyLong());
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

   

    @Test
    void testNumWeeksCourseOfInstructorBySupportWithInvalidSupport() throws Exception {
        when(registrationServices.numWeeksCourseOfInstructorBySupport(anyLong(), any(Support.class)))
                .thenThrow(new IllegalArgumentException("Invalid support type"));

        mockMvc.perform(get("/registration/numWeeks/1/INVALID_SUPPORT"))
                .andExpect(status().isBadRequest()); // Expect 400 for invalid support type

        verify(registrationServices, never()).numWeeksCourseOfInstructorBySupport(anyLong(), any(Support.class));
    }

    @Test
    void testNumWeeksCourseOfInstructorBySupport_EmptyList() throws Exception {
        when(registrationServices.numWeeksCourseOfInstructorBySupport(anyLong(), any(Support.class)))
                .thenReturn(List.of());

        mockMvc.perform(get(API_REGISTRATION_NUM_WEEKS, 1, "SNOWBOARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testNumWeeksCourseOfInstructorBySupportSuccess() throws Exception {
        List<Integer> weeks = Arrays.asList(1, 2, 3);
        when(registrationServices.numWeeksCourseOfInstructorBySupport(anyLong(), any(Support.class))).thenReturn(weeks);

        mockMvc.perform(get("/registration/numWeeks/1/SNOWBOARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2))
                .andExpect(jsonPath("$[2]").value(3));

        verify(registrationServices, times(1)).numWeeksCourseOfInstructorBySupport(anyLong(), any(Support.class));
    }

    @Test
    void testAddAndAssignToSkierAndCourseWithNullDTO() throws Exception {
        RegistrationDTO invalidDTO = new RegistrationDTO();
        invalidDTO.setNumWeek(null); // Invalid DTO

        mockMvc.perform(post("/registration/addAndAssignToSkierAndCourse/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest()); // Expect 400 for invalid input

        verify(registrationServices, never()).addRegistrationAndAssignToSkierAndCourse(any(), anyLong(), anyLong());
    }

    private RegistrationDTO createSampleRegistrationDTO() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(1L);
        dto.setNumWeek(10);
        return dto;
    }
}
