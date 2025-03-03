package tn.esprit.spring.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.services.IRegistrationServices;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationRestController.class)
public class RegistrationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRegistrationServices registrationServices;

    private Registration mockRegistration;

    @BeforeEach
    public void setup() {
        // Create a mock Registration object for testing
        mockRegistration = new Registration();
        mockRegistration.setNumRegistration(1L);
        // Set other necessary fields
    }

    @Test
    public void testAddAndAssignToSkier() throws Exception {
        when(registrationServices.addRegistrationAndAssignToSkier(any(Registration.class), anyLong()))
                .thenReturn(mockRegistration);

        mockMvc.perform(put("/registration/addAndAssignToSkier/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"numRegistration\": 1 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numRegistration").value(1));
    }

    @Test
    public void testAssignToCourse() throws Exception {
        when(registrationServices.assignRegistrationToCourse(anyLong(), anyLong()))
                .thenReturn(mockRegistration);

        mockMvc.perform(put("/registration/assignToCourse/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numRegistration").value(1));
    }

    @Test
    public void testAddAndAssignToSkierAndCourse() throws Exception {
        when(registrationServices.addRegistrationAndAssignToSkierAndCourse(any(Registration.class), anyLong(), anyLong()))
                .thenReturn(mockRegistration);

        mockMvc.perform(put("/registration/addAndAssignToSkierAndCourse/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"numRegistration\": 1 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numRegistration").value(1));
    }

    @Test
    public void testNumWeeksCourseOfInstructorBySupport() throws Exception {
        List<Integer> mockWeeks = Arrays.asList(1, 2, 3);

        when(registrationServices.numWeeksCourseOfInstructorBySupport(anyLong(), any(Support.class)))
                .thenReturn(mockWeeks);

        mockMvc.perform(get("/registration/numWeeks/1/SNOWBOARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2))
                .andExpect(jsonPath("$[2]").value(3));
    }
}