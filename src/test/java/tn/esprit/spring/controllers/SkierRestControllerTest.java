package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.ISkierServices;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SkierRestControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private ISkierServices skierServices;
    
    @InjectMocks
    private SkierRestController skierRestController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(skierRestController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Test
    public void testAddSkier() throws Exception {
        // Prepare test data
        Skier skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("John");
        skier.setLastName("Doe");
        skier.setDateOfBirth(LocalDate.of(1990, 5, 15));
        skier.setCity("Aspen");
        
        when(skierServices.addSkier(any(Skier.class))).thenReturn(skier);
        
        // Perform test
        mockMvc.perform(post("/skier/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skier)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.city", is("Aspen")));
    }
    
    @Test
    public void testAddSkierAndAssignToCourse() throws Exception {
        // Prepare test data
        Skier skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("John");
        skier.setLastName("Doe");
        
        Long courseId = 5L;
        
        when(skierServices.addSkierAndAssignToCourse(any(Skier.class), anyLong())).thenReturn(skier);
        
        // Perform test
        mockMvc.perform(post("/skier/addAndAssign/{numCourse}", courseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skier)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")));
    }
    
    @Test
    public void testAssignToSubscription() throws Exception {
        // Prepare test data
        Skier skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("John");
        skier.setLastName("Doe");
        
        Subscription subscription = new Subscription();
        subscription.setNumSub(3L);
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        
        skier.setSubscription(subscription);
        
        when(skierServices.assignSkierToSubscription(anyLong(), anyLong())).thenReturn(skier);
        
        // Perform test
        mockMvc.perform(put("/skier/assignToSub/{numSkier}/{numSub}", 1L, 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.subscription.numSub", is(3)))
                .andExpect(jsonPath("$.subscription.typeSub", is(TypeSubscription.ANNUAL.toString())));
    }
    
    @Test
    public void testAssignToPiste() throws Exception {
        // Prepare test data
        Skier skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("John");
        skier.setLastName("Doe");
        
        when(skierServices.assignSkierToPiste(anyLong(), anyLong())).thenReturn(skier);
        
        // Perform test
        mockMvc.perform(put("/skier/assignToPiste/{numSkier}/{numPiste}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")));
    }
    
    @Test
    public void testRetrieveSkiersBySubscriptionType() throws Exception {
        // Prepare test data
        Skier skier1 = new Skier(1L, "John", "Doe");
        Skier skier2 = new Skier(2L, "Jane", "Smith");
        
        List<Skier> skiers = Arrays.asList(skier1, skier2);
        
        when(skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.MONTHLY)).thenReturn(skiers);
        
        // Perform test
        mockMvc.perform(get("/skier/getSkiersBySubscription")
                .param("typeSubscription", "MONTHLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numSkier", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].numSkier", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("Jane")));
    }
    
    @Test
    public void testGetSkierById() throws Exception {
        // Prepare test data
        Skier skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("John");
        skier.setLastName("Doe");
        skier.setDateOfBirth(LocalDate.of(1990, 5, 15));
        skier.setCity("Aspen");
        
        when(skierServices.retrieveSkier(1L)).thenReturn(skier);
        
        // Perform test
        mockMvc.perform(get("/skier/get/{id-skier}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSkier", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));
    }
    
    @Test
    public void testDeleteSkier() throws Exception {
        // Prepare test data
        doNothing().when(skierServices).removeSkier(anyLong());
        
        // Perform test
        mockMvc.perform(delete("/skier/delete/{id-skier}", 1L))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testGetAllSkiers() throws Exception {
        // Prepare test data
        Skier skier1 = new Skier(1L, "John", "Doe");
        Skier skier2 = new Skier(2L, "Jane", "Smith");
        
        List<Skier> skiers = Arrays.asList(skier1, skier2);
        
        when(skierServices.retrieveAllSkiers()).thenReturn(skiers);
        
        // Perform test
        mockMvc.perform(get("/skier/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numSkier", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].numSkier", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("Jane")));
    }
}
