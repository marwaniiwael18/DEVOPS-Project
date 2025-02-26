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
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.services.ISkierServices;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SkierRestController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class SkierRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISkierServices skierServices;

    @Autowired
    private ObjectMapper objectMapper;

    private Skier skier;

    @BeforeEach
    void setUp() {
        skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("John Doe");
    }

    @Test
    void testAddSkierSuccess() throws Exception {
        when(skierServices.addSkier(any(Skier.class))).thenReturn(skier);

        mockMvc.perform(post("/skier/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skier)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier").value(skier.getNumSkier()));

        verify(skierServices, times(1)).addSkier(any(Skier.class));
    }

    @Test
    void testGetSkierByIdSuccess() throws Exception {
        when(skierServices.retrieveSkier(1L)).thenReturn(skier);

        mockMvc.perform(get("/skier/get/{id-skier}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier").value(skier.getNumSkier()));

        verify(skierServices, times(1)).retrieveSkier(1L);
    }

    @Test
    void testGetAllSkiersSuccess() throws Exception {
        List<Skier> skiers = Arrays.asList(skier, new Skier(2L, "Jane Doe"));
        when(skierServices.retrieveAllSkiers()).thenReturn(skiers);

        mockMvc.perform(get("/skier/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(skiers.size()));

        verify(skierServices, times(1)).retrieveAllSkiers();
    }

    @Test
    void testDeleteSkierSuccess() throws Exception {
        Long skierId = 1L;
        when(skierServices.removeSkier(skierId)).thenReturn(true);

        mockMvc.perform(delete("/skier/{id}", skierId))
                .andExpect(status().isNoContent());

        verify(skierServices, times(1)).removeSkier(skierId);
    }

    @Test
    void testGetSkierByIdNotFound() throws Exception {
        Long skierId = 99L;
        when(skierServices.retrieveSkier(skierId)).thenReturn(null);

        mockMvc.perform(get("/skier/{id}", skierId))
                .andExpect(status().isNotFound());

        verify(skierServices, times(1)).retrieveSkier(skierId);
    }
}