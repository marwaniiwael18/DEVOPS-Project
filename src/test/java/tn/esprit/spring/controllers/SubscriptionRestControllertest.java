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
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.ISubscriptionServices;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionRestControllertest {
    
    private MockMvc mockMvc;
    
    @Mock
    private ISubscriptionServices subscriptionServices;
    
    @InjectMocks
    private SubscriptionRestController subscriptionRestController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionRestController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Test
    public void testAddSubscription() throws Exception {
        // Prepare test data
        Subscription subscription = new Subscription();
        subscription.setNumSub(1L);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(6));
        subscription.setPrice(100.0f);
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        
        when(subscriptionServices.addSubscription(any(Subscription.class))).thenReturn(subscription);
        
        // Perform test
        mockMvc.perform(post("/subscription/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSub", is(1)))
                .andExpect(jsonPath("$.typeSub", is(TypeSubscription.ANNUAL.toString())));
        
        verify(subscriptionServices, times(1)).addSubscription(any(Subscription.class));
    }
    
    @Test
    public void testGetSubscriptionById() throws Exception {
        // Prepare test data
        Subscription subscription = new Subscription();
        subscription.setNumSub(1L);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(6));
        subscription.setPrice(100.0f);
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        
        when(subscriptionServices.retrieveSubscriptionById(1L)).thenReturn(subscription);
        
        // Perform test
        mockMvc.perform(get("/subscription/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSub", is(1)))
                .andExpect(jsonPath("$.typeSub", is(TypeSubscription.ANNUAL.toString())));
        
        verify(subscriptionServices, times(1)).retrieveSubscriptionById(1L);
    }
    
    @Test
    public void testGetSubscriptionsByType() throws Exception {
        // Prepare test data
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(1L);
        subscription1.setTypeSub(TypeSubscription.MONTHLY);
        
        Subscription subscription2 = new Subscription();
        subscription2.setNumSub(2L);
        subscription2.setTypeSub(TypeSubscription.MONTHLY);
        
        Set<Subscription> subscriptions = new HashSet<>(Arrays.asList(subscription1, subscription2));
        
        when(subscriptionServices.getSubscriptionByType(TypeSubscription.MONTHLY)).thenReturn(subscriptions);
        
        // Perform test
        mockMvc.perform(get("/subscription/all/MONTHLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        
        verify(subscriptionServices, times(1)).getSubscriptionByType(TypeSubscription.MONTHLY);
    }
    
    @Test
    public void testUpdateSubscription() throws Exception {
        // Prepare test data
        Subscription subscription = new Subscription();
        subscription.setNumSub(1L);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(6));
        subscription.setPrice(150.0f); // Updated price
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        
        when(subscriptionServices.updateSubscription(any(Subscription.class))).thenReturn(subscription);
        
        // Perform test
        mockMvc.perform(put("/subscription/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSub", is(1)))
                .andExpect(jsonPath("$.price", is(150.0)));
        
        verify(subscriptionServices, times(1)).updateSubscription(any(Subscription.class));
    }
    
    @Test
    public void testGetSubscriptionsByDates() throws Exception {
        // Prepare test data
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(1L);
        subscription1.setStartDate(LocalDate.of(2023, 3, 15));
        
        Subscription subscription2 = new Subscription();
        subscription2.setNumSub(2L);
        subscription2.setStartDate(LocalDate.of(2023, 6, 10));
        
        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
        
        when(subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate)).thenReturn(subscriptions);
        
        // Perform test
        mockMvc.perform(get("/subscription/all/2023-01-01/2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numSub", is(1)))
                .andExpect(jsonPath("$[1].numSub", is(2)));
        
        verify(subscriptionServices, times(1)).retrieveSubscriptionsByDates(startDate, endDate);
    }

    @Test
    public void testGetAllSubscriptions() throws Exception {
        // Prepare test data
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(1L);
        subscription1.setStartDate(LocalDate.now());
        subscription1.setEndDate(LocalDate.now().plusMonths(3));
        subscription1.setPrice(100.0f);
        subscription1.setTypeSub(TypeSubscription.MONTHLY);
        
        Subscription subscription2 = new Subscription();
        subscription2.setNumSub(2L);
        subscription2.setStartDate(LocalDate.now());
        subscription2.setEndDate(LocalDate.now().plusYears(1));
        subscription2.setPrice(500.0f);
        subscription2.setTypeSub(TypeSubscription.ANNUAL);
        
        List<Subscription> allSubscriptions = Arrays.asList(subscription1, subscription2);
        
        when(subscriptionServices.retrieveAllSubscriptions()).thenReturn(allSubscriptions);
        
        // Perform test
        mockMvc.perform(get("/subscription/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numSub", is(1)))
                .andExpect(jsonPath("$[0].typeSub", is(TypeSubscription.MONTHLY.toString())))
                .andExpect(jsonPath("$[0].price", is(100.0)))
                .andExpect(jsonPath("$[1].numSub", is(2)))
                .andExpect(jsonPath("$[1].typeSub", is(TypeSubscription.ANNUAL.toString())))
                .andExpect(jsonPath("$[1].price", is(500.0)));
        
        verify(subscriptionServices, times(1)).retrieveAllSubscriptions();
    }
    
    @Test
    public void testGetSubscriptionsByTypeWithSEMESTRIELType() throws Exception {
        // Prepare test data
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(1L);
        subscription1.setTypeSub(TypeSubscription.SEMESTRIEL);
        
        Subscription subscription2 = new Subscription();
        subscription2.setNumSub(2L);
        subscription2.setTypeSub(TypeSubscription.SEMESTRIEL);
        
        Set<Subscription> subscriptions = new HashSet<>(Arrays.asList(subscription1, subscription2));
        
        when(subscriptionServices.getSubscriptionByType(TypeSubscription.SEMESTRIEL)).thenReturn(subscriptions);
        
        // Perform test
        mockMvc.perform(get("/subscription/all/SEMESTRIEL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        
        verify(subscriptionServices, times(1)).getSubscriptionByType(TypeSubscription.SEMESTRIEL);
    }
    
    @Test
    public void testGetSubscriptionsByTypeWithANNUALType() throws Exception {
        // Prepare test data
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(1L);
        subscription1.setTypeSub(TypeSubscription.ANNUAL);
        
        Subscription subscription2 = new Subscription();
        subscription2.setNumSub(2L);
        subscription2.setTypeSub(TypeSubscription.ANNUAL);
        
        Set<Subscription> subscriptions = new HashSet<>(Arrays.asList(subscription1, subscription2));
        
        when(subscriptionServices.getSubscriptionByType(TypeSubscription.ANNUAL)).thenReturn(subscriptions);
        
        // Perform test
        mockMvc.perform(get("/subscription/all/ANNUAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        
        verify(subscriptionServices, times(1)).getSubscriptionByType(TypeSubscription.ANNUAL);
    }
    
    @Test
    public void testGetSubscriptionsByDatesWithDifferentDateRange() throws Exception {
        // Prepare test data
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 6, 30);
        
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(1L);
        subscription1.setStartDate(LocalDate.of(2022, 2, 15));
        
        List<Subscription> subscriptions = Arrays.asList(subscription1);
        
        when(subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate)).thenReturn(subscriptions);
        
        // Perform test
        mockMvc.perform(get("/subscription/all/2022-01-01/2022-06-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numSub", is(1)));
        
        verify(subscriptionServices, times(1)).retrieveSubscriptionsByDates(startDate, endDate);
    }
    
    @Test
    public void testEmptySubscriptions() throws Exception {
        // Prepare test data - empty list
        List<Subscription> emptyList = Arrays.asList();
        
        when(subscriptionServices.retrieveAllSubscriptions()).thenReturn(emptyList);
        
        // Perform test
        mockMvc.perform(get("/subscription/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        
        verify(subscriptionServices, times(1)).retrieveAllSubscriptions();
    }
}
