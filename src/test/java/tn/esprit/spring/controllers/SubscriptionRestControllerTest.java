package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.ISubscriptionServices;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionRestController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class SubscriptionRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISubscriptionServices subscriptionServices;

    @Autowired
    private ObjectMapper objectMapper;

    private Subscription subscription;

    private static final String SUBSCRIPTION_ADD_ENDPOINT = "/subscription/add";
    private static final String SUBSCRIPTION_GET_ENDPOINT = "/subscription/get/{id-subscription}";
    private static final String SUBSCRIPTION_UPDATE_ENDPOINT = "/subscription/update";
    private static final String SUBSCRIPTIONS_BY_TYPE_ENDPOINT = "/subscription/all/{typeSub}";
    private static final String SUBSCRIPTIONS_BY_DATES_ENDPOINT = "/subscription/all/{date1}/{date2}";

    @BeforeEach
    void setUp() {
        subscription = new Subscription();
        subscription.setNumSub(1L);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setPrice(100.0f);
        subscription.setTypeSub(TypeSubscription.MONTHLY);
    }

    @Test
    void testAddSubscriptionSuccess() throws Exception {
        when(subscriptionServices.addSubscription(any(Subscription.class))).thenReturn(subscription);

        mockMvc.perform(post(SUBSCRIPTION_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSub").value(subscription.getNumSub()));

        verify(subscriptionServices, times(1)).addSubscription(any(Subscription.class));
    }

    @Test
    void testGetSubscriptionByIdSuccess() throws Exception {
        when(subscriptionServices.retrieveSubscriptionById(1L)).thenReturn(subscription);

        mockMvc.perform(get(SUBSCRIPTION_GET_ENDPOINT, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numSub").value(1L));

        verify(subscriptionServices, times(1)).retrieveSubscriptionById(1L);
    }

    @Test
    void testGetSubscriptionByIdNotFound() throws Exception {
        when(subscriptionServices.retrieveSubscriptionById(5L)).thenReturn(null);

        mockMvc.perform(get(SUBSCRIPTION_GET_ENDPOINT, 5))
                .andExpect(status().isNotFound());

        verify(subscriptionServices, times(1)).retrieveSubscriptionById(5L);
    }

    @Test
    void testUpdateSubscriptionSuccess() throws Exception {
        subscription.setPrice(120.0f);
        when(subscriptionServices.updateSubscription(any(Subscription.class))).thenReturn(subscription);

        mockMvc.perform(put(SUBSCRIPTION_UPDATE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(120.0f));

        verify(subscriptionServices, times(1)).updateSubscription(any(Subscription.class));
    }

    @Test
    void testGetSubscriptionsByTypeSuccess() throws Exception {
        Set<Subscription> subscriptions = new HashSet<>();
        subscriptions.add(subscription);

        when(subscriptionServices.getSubscriptionByType(TypeSubscription.MONTHLY)).thenReturn(subscriptions);

        mockMvc.perform(get(SUBSCRIPTIONS_BY_TYPE_ENDPOINT, TypeSubscription.MONTHLY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(subscriptions.size()));

        verify(subscriptionServices, times(1)).getSubscriptionByType(TypeSubscription.MONTHLY);
    }

    @Test
    void testGetSubscriptionsByDatesSuccess() throws Exception {
        List<Subscription> subscriptions = List.of(subscription);
        when(subscriptionServices.retrieveSubscriptionsByDates(any(LocalDate.class), any(LocalDate.class))).thenReturn(subscriptions);

        mockMvc.perform(get(SUBSCRIPTIONS_BY_DATES_ENDPOINT, LocalDate.now(), LocalDate.now().plusMonths(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(subscriptions.size()));

        verify(subscriptionServices, times(1)).retrieveSubscriptionsByDates(any(LocalDate.class), any(LocalDate.class));
    }
}