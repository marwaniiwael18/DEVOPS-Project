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
    
    // API endpoints constants
    private static final String API_SUBSCRIPTION_ADD = "/subscription/add";
    private static final String API_SUBSCRIPTION_GET = "/subscription/get/{id}";
    private static final String API_SUBSCRIPTION_ALL = "/subscription/all";
    private static final String API_SUBSCRIPTION_TYPE = "/subscription/all/{type}";
    private static final String API_SUBSCRIPTION_UPDATE = "/subscription/update";
    private static final String API_SUBSCRIPTION_DATES = "/subscription/all/{startDate}/{endDate}";
    
    // JSON path constants
    private static final String JSON_PATH_NUM_SUB = "$.numSub";
    private static final String JSON_PATH_PRICE = "$.price";
    private static final String JSON_PATH_TYPE_SUB = "$.typeSub";
    private static final String JSON_PATH_ARRAY_SIZE = "$";
    private static final String JSON_PATH_INDEX_0_NUM_SUB = "$[0].numSub";
    private static final String JSON_PATH_INDEX_0_TYPE_SUB = "$[0].typeSub";
    private static final String JSON_PATH_INDEX_0_PRICE = "$[0].price";
    private static final String JSON_PATH_INDEX_1_NUM_SUB = "$[1].numSub";
    private static final String JSON_PATH_INDEX_1_TYPE_SUB = "$[1].typeSub";
    private static final String JSON_PATH_INDEX_1_PRICE = "$[1].price";
    
    // Test data constants
    private static final Long SUBSCRIPTION_ID_1 = 1L;
    private static final Long SUBSCRIPTION_ID_2 = 2L;
    private static final Float SUBSCRIPTION_PRICE_1 = 100.0f;
    private static final Float SUBSCRIPTION_PRICE_2 = 500.0f;
    private static final Float UPDATED_PRICE = 150.0f;
    
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
        subscription.setNumSub(SUBSCRIPTION_ID_1);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(6));
        subscription.setPrice(SUBSCRIPTION_PRICE_1);
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        
        when(subscriptionServices.addSubscription(any(Subscription.class))).thenReturn(subscription);
        
        // Perform test
        mockMvc.perform(post(API_SUBSCRIPTION_ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NUM_SUB, is(SUBSCRIPTION_ID_1.intValue())))
                .andExpect(jsonPath(JSON_PATH_TYPE_SUB, is(TypeSubscription.ANNUAL.toString())));
        
        verify(subscriptionServices, times(1)).addSubscription(any(Subscription.class));
    }
    
    @Test
    public void testGetSubscriptionById() throws Exception {
        // Prepare test data
        Subscription subscription = new Subscription();
        subscription.setNumSub(SUBSCRIPTION_ID_1);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(6));
        subscription.setPrice(SUBSCRIPTION_PRICE_1);
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        
        when(subscriptionServices.retrieveSubscriptionById(SUBSCRIPTION_ID_1)).thenReturn(subscription);
        
        // Perform test
        mockMvc.perform(get(API_SUBSCRIPTION_GET, SUBSCRIPTION_ID_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NUM_SUB, is(SUBSCRIPTION_ID_1.intValue())))
                .andExpect(jsonPath(JSON_PATH_TYPE_SUB, is(TypeSubscription.ANNUAL.toString())));
        
        verify(subscriptionServices, times(1)).retrieveSubscriptionById(SUBSCRIPTION_ID_1);
    }
    
    @Test
    public void testGetSubscriptionsByType() throws Exception {
        // Prepare test data
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(SUBSCRIPTION_ID_1);
        subscription1.setTypeSub(TypeSubscription.MONTHLY);
        
        Subscription subscription2 = new Subscription();
        subscription2.setNumSub(SUBSCRIPTION_ID_2);
        subscription2.setTypeSub(TypeSubscription.MONTHLY);
        
        Set<Subscription> subscriptions = new HashSet<>(Arrays.asList(subscription1, subscription2));
        
        when(subscriptionServices.getSubscriptionByType(TypeSubscription.MONTHLY)).thenReturn(subscriptions);
        
        // Perform test
        mockMvc.perform(get(API_SUBSCRIPTION_TYPE, "MONTHLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ARRAY_SIZE, hasSize(2)));
        
        verify(subscriptionServices, times(1)).getSubscriptionByType(TypeSubscription.MONTHLY);
    }
    
    @Test
    public void testUpdateSubscription() throws Exception {
        // Prepare test data
        Subscription subscription = new Subscription();
        subscription.setNumSub(SUBSCRIPTION_ID_1);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(6));
        subscription.setPrice(UPDATED_PRICE); // Updated price
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        
        when(subscriptionServices.updateSubscription(any(Subscription.class))).thenReturn(subscription);
        
        // Perform test
        mockMvc.perform(put(API_SUBSCRIPTION_UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NUM_SUB, is(SUBSCRIPTION_ID_1.intValue())))
                .andExpect(jsonPath(JSON_PATH_PRICE, is(UPDATED_PRICE.doubleValue())));
        
        verify(subscriptionServices, times(1)).updateSubscription(any(Subscription.class));
    }
    
    @Test
    public void testGetSubscriptionsByDates() throws Exception {
        // Prepare test data
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(SUBSCRIPTION_ID_1);
        subscription1.setStartDate(LocalDate.of(2023, 3, 15));
        
        Subscription subscription2 = new Subscription();
        subscription2.setNumSub(SUBSCRIPTION_ID_2);
        subscription2.setStartDate(LocalDate.of(2023, 6, 10));
        
        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
        
        when(subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate)).thenReturn(subscriptions);
        
        // Perform test
        mockMvc.perform(get(API_SUBSCRIPTION_DATES, "2023-01-01", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ARRAY_SIZE, hasSize(2)))
                .andExpect(jsonPath(JSON_PATH_INDEX_0_NUM_SUB, is(SUBSCRIPTION_ID_1.intValue())))
                .andExpect(jsonPath(JSON_PATH_INDEX_1_NUM_SUB, is(SUBSCRIPTION_ID_2.intValue())));
        
        verify(subscriptionServices, times(1)).retrieveSubscriptionsByDates(startDate, endDate);
    }

    @Test
    public void testGetAllSubscriptions() throws Exception {
        // Prepare test data
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(SUBSCRIPTION_ID_1);
        subscription1.setStartDate(LocalDate.now());
        subscription1.setEndDate(LocalDate.now().plusMonths(3));
        subscription1.setPrice(SUBSCRIPTION_PRICE_1);
        subscription1.setTypeSub(TypeSubscription.MONTHLY);
        
        Subscription subscription2 = new Subscription();
        subscription2.setNumSub(SUBSCRIPTION_ID_2);
        subscription2.setStartDate(LocalDate.now());
        subscription2.setEndDate(LocalDate.now().plusYears(1));
        subscription2.setPrice(SUBSCRIPTION_PRICE_2);
        subscription2.setTypeSub(TypeSubscription.ANNUAL);
        
        List<Subscription> allSubscriptions = Arrays.asList(subscription1, subscription2);
        
        when(subscriptionServices.retrieveAllSubscriptions()).thenReturn(allSubscriptions);
        
        // Perform test
        mockMvc.perform(get(API_SUBSCRIPTION_ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(JSON_PATH_ARRAY_SIZE, hasSize(2)))
                .andExpect(jsonPath(JSON_PATH_INDEX_0_NUM_SUB, is(SUBSCRIPTION_ID_1.intValue())))
                .andExpect(jsonPath(JSON_PATH_INDEX_0_TYPE_SUB, is(TypeSubscription.MONTHLY.toString())))
                .andExpect(jsonPath(JSON_PATH_INDEX_0_PRICE, is(SUBSCRIPTION_PRICE_1.doubleValue())))
                .andExpect(jsonPath(JSON_PATH_INDEX_1_NUM_SUB, is(SUBSCRIPTION_ID_2.intValue())))
                .andExpect(jsonPath(JSON_PATH_INDEX_1_TYPE_SUB, is(TypeSubscription.ANNUAL.toString())))
                .andExpect(jsonPath(JSON_PATH_INDEX_1_PRICE, is(SUBSCRIPTION_PRICE_2.doubleValue())));
        
        verify(subscriptionServices, times(1)).retrieveAllSubscriptions();
    }
    
    @Test
    public void testGetSubscriptionsByTypeWithSEMESTRIELType() throws Exception {
        // Prepare test data
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(SUBSCRIPTION_ID_1);
        subscription1.setTypeSub(TypeSubscription.SEMESTRIEL);
        
        Subscription subscription2 = new Subscription();
        subscription2.setNumSub(SUBSCRIPTION_ID_2);
        subscription2.setTypeSub(TypeSubscription.SEMESTRIEL);
        
        Set<Subscription> subscriptions = new HashSet<>(Arrays.asList(subscription1, subscription2));
        
        when(subscriptionServices.getSubscriptionByType(TypeSubscription.SEMESTRIEL)).thenReturn(subscriptions);
        
        // Perform test
        mockMvc.perform(get(API_SUBSCRIPTION_TYPE, "SEMESTRIEL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ARRAY_SIZE, hasSize(2)));
        
        verify(subscriptionServices, times(1)).getSubscriptionByType(TypeSubscription.SEMESTRIEL);
    }
    
    @Test
    public void testGetSubscriptionsByTypeWithANNUALType() throws Exception {
        // Prepare test data
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(SUBSCRIPTION_ID_1);
        subscription1.setTypeSub(TypeSubscription.ANNUAL);
        
        Subscription subscription2 = new Subscription();
        subscription2.setNumSub(SUBSCRIPTION_ID_2);
        subscription2.setTypeSub(TypeSubscription.ANNUAL);
        
        Set<Subscription> subscriptions = new HashSet<>(Arrays.asList(subscription1, subscription2));
        
        when(subscriptionServices.getSubscriptionByType(TypeSubscription.ANNUAL)).thenReturn(subscriptions);
        
        // Perform test
        mockMvc.perform(get(API_SUBSCRIPTION_TYPE, "ANNUAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ARRAY_SIZE, hasSize(2)));
        
        verify(subscriptionServices, times(1)).getSubscriptionByType(TypeSubscription.ANNUAL);
    }
    
    @Test
    public void testGetSubscriptionsByDatesWithDifferentDateRange() throws Exception {
        // Prepare test data
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 6, 30);
        
        Subscription subscription1 = new Subscription();
        subscription1.setNumSub(SUBSCRIPTION_ID_1);
        subscription1.setStartDate(LocalDate.of(2022, 2, 15));
        
        List<Subscription> subscriptions = Arrays.asList(subscription1);
        
        when(subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate)).thenReturn(subscriptions);
        
        // Perform test
        mockMvc.perform(get(API_SUBSCRIPTION_DATES, "2022-01-01", "2022-06-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ARRAY_SIZE, hasSize(1)))
                .andExpect(jsonPath(JSON_PATH_INDEX_0_NUM_SUB, is(SUBSCRIPTION_ID_1.intValue())));
        
        verify(subscriptionServices, times(1)).retrieveSubscriptionsByDates(startDate, endDate);
    }
    
    @Test
    public void testEmptySubscriptions() throws Exception {
        // Prepare test data - empty list
        List<Subscription> emptyList = Arrays.asList();
        
        when(subscriptionServices.retrieveAllSubscriptions()).thenReturn(emptyList);
        
        // Perform test
        mockMvc.perform(get(API_SUBSCRIPTION_ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ARRAY_SIZE, hasSize(0)));
        
        verify(subscriptionServices, times(1)).retrieveAllSubscriptions();
    }
}
