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

    // API endpoints
    private static final String API_SKIER_ADD = "/skier/add";
    private static final String API_SKIER_ADD_AND_ASSIGN = "/skier/addAndAssign/{numCourse}";
    private static final String API_SKIER_ASSIGN_TO_SUB = "/skier/assignToSub/{numSkier}/{numSub}";
    private static final String API_SKIER_ASSIGN_TO_PISTE = "/skier/assignToPiste/{numSkier}/{numPiste}";
    private static final String API_SKIER_GET_BY_SUBSCRIPTION = "/skier/getSkiersBySubscription";
    private static final String API_SKIER_GET = "/skier/get/{id-skier}";
    private static final String API_SKIER_DELETE = "/skier/delete/{id-skier}";
    private static final String API_SKIER_ALL = "/skier/all";
    
    // JSON paths
    private static final String JSON_PATH_NUM_SKIER = "$.numSkier";
    private static final String JSON_PATH_FIRST_NAME = "$.firstName";
    private static final String JSON_PATH_LAST_NAME = "$.lastName";
    private static final String JSON_PATH_CITY = "$.city";
    private static final String JSON_PATH_SUBSCRIPTION_NUM = "$.subscription.numSub";
    private static final String JSON_PATH_SUBSCRIPTION_TYPE = "$.subscription.typeSub";
    
    // Test data constants
    private static final String SKIER_CITY = "Aspen";
    private static final String FIRST_NAME_JOHN = "John";
    private static final String FIRST_NAME_JANE = "Jane";
    private static final String LAST_NAME_DOE = "Doe";
    private static final String LAST_NAME_SMITH = "Smith";
    private static final Long SKIER_ID_1 = 1L;
    private static final Long SKIER_ID_2 = 2L;
    private static final Long COURSE_ID = 5L;
    private static final Long SUBSCRIPTION_ID = 3L;
    private static final Long PISTE_ID = 2L;

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
        skier.setNumSkier(SKIER_ID_1);
        skier.setFirstName(FIRST_NAME_JOHN);
        skier.setLastName(LAST_NAME_DOE);
        skier.setDateOfBirth(LocalDate.of(1990, 5, 15));
        skier.setCity(SKIER_CITY);

        when(skierServices.addSkier(any(Skier.class))).thenReturn(skier);

        // Perform test
        mockMvc.perform(post(API_SKIER_ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skier)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NUM_SKIER, is(SKIER_ID_1.intValue())))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME, is(FIRST_NAME_JOHN)))
                .andExpect(jsonPath(JSON_PATH_LAST_NAME, is(LAST_NAME_DOE)))
                .andExpect(jsonPath(JSON_PATH_CITY, is(SKIER_CITY)));
    }

    @Test
    public void testAddSkierAndAssignToCourse() throws Exception {
        // Prepare test data
        Skier skier = new Skier();
        skier.setNumSkier(SKIER_ID_1);
        skier.setFirstName(FIRST_NAME_JOHN);
        skier.setLastName(LAST_NAME_DOE);

        when(skierServices.addSkierAndAssignToCourse(any(Skier.class), anyLong())).thenReturn(skier);

        // Perform test
        mockMvc.perform(post(API_SKIER_ADD_AND_ASSIGN, COURSE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skier)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NUM_SKIER, is(SKIER_ID_1.intValue())))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME, is(FIRST_NAME_JOHN)));
    }

    @Test
    public void testAssignToSubscription() throws Exception {
        // Prepare test data
        Skier skier = new Skier();
        skier.setNumSkier(SKIER_ID_1);
        skier.setFirstName(FIRST_NAME_JOHN);
        skier.setLastName(LAST_NAME_DOE);

        Subscription subscription = new Subscription();
        subscription.setNumSub(SUBSCRIPTION_ID);
        subscription.setTypeSub(TypeSubscription.ANNUAL);

        skier.setSubscription(subscription);

        when(skierServices.assignSkierToSubscription(anyLong(), anyLong())).thenReturn(skier);

        // Perform test
        mockMvc.perform(put(API_SKIER_ASSIGN_TO_SUB, SKIER_ID_1, SUBSCRIPTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NUM_SKIER, is(SKIER_ID_1.intValue())))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME, is(FIRST_NAME_JOHN)))
                .andExpect(jsonPath(JSON_PATH_SUBSCRIPTION_NUM, is(SUBSCRIPTION_ID.intValue())))
                .andExpect(jsonPath(JSON_PATH_SUBSCRIPTION_TYPE, is(TypeSubscription.ANNUAL.toString())));
    }

    @Test
    public void testAssignToPiste() throws Exception {
        // Prepare test data
        Skier skier = new Skier();
        skier.setNumSkier(SKIER_ID_1);
        skier.setFirstName(FIRST_NAME_JOHN);
        skier.setLastName(LAST_NAME_DOE);

        when(skierServices.assignSkierToPiste(anyLong(), anyLong())).thenReturn(skier);

        // Perform test
        mockMvc.perform(put(API_SKIER_ASSIGN_TO_PISTE, SKIER_ID_1, PISTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NUM_SKIER, is(SKIER_ID_1.intValue())))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME, is(FIRST_NAME_JOHN)));
    }

    @Test
    public void testRetrieveSkiersBySubscriptionType() throws Exception {
        // Prepare test data
        Skier skier1 = new Skier(SKIER_ID_1, FIRST_NAME_JOHN, LAST_NAME_DOE);
        Skier skier2 = new Skier(SKIER_ID_2, FIRST_NAME_JANE, LAST_NAME_SMITH);

        List<Skier> skiers = Arrays.asList(skier1, skier2);

        when(skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.MONTHLY)).thenReturn(skiers);

        // Perform test
        mockMvc.perform(get(API_SKIER_GET_BY_SUBSCRIPTION)
                .param("typeSubscription", "MONTHLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numSkier", is(SKIER_ID_1.intValue())))
                .andExpect(jsonPath("$[0].firstName", is(FIRST_NAME_JOHN)))
                .andExpect(jsonPath("$[1].numSkier", is(SKIER_ID_2.intValue())))
                .andExpect(jsonPath("$[1].firstName", is(FIRST_NAME_JANE)));
    }

    @Test
    public void testGetSkierById() throws Exception {
        // Prepare test data
        Skier skier = new Skier();
        skier.setNumSkier(SKIER_ID_1);
        skier.setFirstName(FIRST_NAME_JOHN);
        skier.setLastName(LAST_NAME_DOE);
        skier.setDateOfBirth(LocalDate.of(1990, 5, 15));
        skier.setCity(SKIER_CITY);

        when(skierServices.retrieveSkier(SKIER_ID_1)).thenReturn(skier);

        // Perform test
        mockMvc.perform(get(API_SKIER_GET, SKIER_ID_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NUM_SKIER, is(SKIER_ID_1.intValue())))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME, is(FIRST_NAME_JOHN)))
                .andExpect(jsonPath(JSON_PATH_LAST_NAME, is(LAST_NAME_DOE)));
    }

    @Test
    public void testDeleteSkier() throws Exception {
        // Prepare test data
        doNothing().when(skierServices).removeSkier(anyLong());

        // Perform test
        mockMvc.perform(delete(API_SKIER_DELETE, SKIER_ID_1))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllSkiers() throws Exception {
        // Prepare test data
        Skier skier1 = new Skier(SKIER_ID_1, FIRST_NAME_JOHN, LAST_NAME_DOE);
        Skier skier2 = new Skier(SKIER_ID_2, FIRST_NAME_JANE, LAST_NAME_SMITH);

        List<Skier> skiers = Arrays.asList(skier1, skier2);

        when(skierServices.retrieveAllSkiers()).thenReturn(skiers);

        // Perform test
        mockMvc.perform(get(API_SKIER_ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numSkier", is(SKIER_ID_1.intValue())))
                .andExpect(jsonPath("$[0].firstName", is(FIRST_NAME_JOHN)))
                .andExpect(jsonPath("$[1].numSkier", is(SKIER_ID_2.intValue())))
                .andExpect(jsonPath("$[1].firstName", is(FIRST_NAME_JANE)));
    }
}
