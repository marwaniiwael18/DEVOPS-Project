package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.services.IPisteServices;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PisteRestControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private IPisteServices pisteServices;
    
    @InjectMocks
    private PisteRestController pisteRestController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(pisteRestController).build();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    public void testAddPiste() throws Exception {
        // Prepare test data
        Piste piste = new Piste();
        piste.setNumPiste(1L);
        piste.setNamePiste("Alpine Run");
        piste.setColor(Color.GREEN);
        piste.setLength(1500);
        piste.setSlope(20);
        
        when(pisteServices.addPiste(any(Piste.class))).thenReturn(piste);
        
        // Perform test
        mockMvc.perform(post("/piste/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(piste)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numPiste", is(1)))
                .andExpect(jsonPath("$.namePiste", is("Alpine Run")))
                .andExpect(jsonPath("$.color", is(Color.GREEN.toString())))
                .andExpect(jsonPath("$.length", is(1500)))
                .andExpect(jsonPath("$.slope", is(20)));
    }
    
    @Test
    public void testGetAllPistes() throws Exception {
        // Prepare test data
        Piste piste1 = new Piste(1L, "Alpine Run", 1500);
        piste1.setColor(Color.GREEN);
        piste1.setSlope(20);
        
        Piste piste2 = new Piste(2L, "Black Diamond", 2500);
        piste2.setColor(Color.BLACK);
        piste2.setSlope(45);
        
        List<Piste> pistes = Arrays.asList(piste1, piste2);
        
        when(pisteServices.retrieveAllPistes()).thenReturn(pistes);
        
        // Perform test
        mockMvc.perform(get("/piste/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numPiste", is(1)))
                .andExpect(jsonPath("$[0].namePiste", is("Alpine Run")))
                .andExpect(jsonPath("$[1].numPiste", is(2)))
                .andExpect(jsonPath("$[1].namePiste", is("Black Diamond")));
    }
    
    @Test
    public void testGetPisteById() throws Exception {
        // Prepare test data
        Piste piste = new Piste();
        piste.setNumPiste(1L);
        piste.setNamePiste("Alpine Run");
        piste.setColor(Color.GREEN);
        piste.setLength(1500);
        piste.setSlope(20);
        
        when(pisteServices.retrievePiste(1L)).thenReturn(piste);
        
        // Perform test
        mockMvc.perform(get("/piste/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numPiste", is(1)))
                .andExpect(jsonPath("$.namePiste", is("Alpine Run")))
                .andExpect(jsonPath("$.color", is(Color.GREEN.toString())));
    }
    
    @Test
    public void testDeletePiste() throws Exception {
        // Prepare test data
        doNothing().when(pisteServices).removePiste(anyLong());
        
        // Perform test
        mockMvc.perform(delete("/piste/delete/1"))
                .andExpect(status().isOk());
    }
}
