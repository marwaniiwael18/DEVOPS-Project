package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.ISubscriptionServices;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionRestController.class)
class SubscriptionRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISubscriptionServices subscriptionServices;

    private ObjectMapper objectMapper;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testSubscription = new Subscription();
        testSubscription.setNumSub(1L);
        testSubscription.setStartDate(LocalDate.now());
        testSubscription.setEndDate(LocalDate.now().plusMonths(1));
        testSubscription.setPrice(100.0f);
        testSubscription.setTypeSub(TypeSubscription.MONTHLY);
    }

    @Nested
    @DisplayName("Create Subscription Tests")
    class CreateSubscriptionTests {

        @Test
        @DisplayName("Should create subscription successfully")
        void shouldCreateSubscription() throws Exception {
            when(subscriptionServices.addSubscription(any(Subscription.class)))
                    .thenReturn(testSubscription);

            mockMvc.perform(post("/api/v1/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSubscription)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.numSub", is(1)))
                    .andExpect(jsonPath("$.price", is(100.0)))
                    .andExpect(jsonPath("$.typeSub", is("MONTHLY")))
                    .andExpect(jsonPath("$.startDate", notNullValue()))
                    .andExpect(jsonPath("$.endDate", notNullValue()));

            verify(subscriptionServices).addSubscription(any(Subscription.class));
        }

        @Test
        @DisplayName("Should reject subscription with negative price")
        void shouldRejectSubscriptionWithNegativePrice() throws Exception {
            testSubscription.setPrice(-100.0f);

            mockMvc.perform(post("/api/v1/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSubscription)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(subscriptionServices, never()).addSubscription(any());
        }

        @Test
        @DisplayName("Should reject subscription with missing required fields")
        void shouldRejectSubscriptionWithMissingFields() throws Exception {
            Subscription invalidSubscription = new Subscription();

            mockMvc.perform(post("/api/v1/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidSubscription)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(subscriptionServices, never()).addSubscription(any());
        }
    }

    @Nested
    @DisplayName("Retrieve Subscription Tests")
    class RetrieveSubscriptionTests {

        @Test
        @DisplayName("Should get subscription by ID")
        void shouldGetSubscriptionById() throws Exception {
            when(subscriptionServices.retrieveSubscriptionById(1L))
                    .thenReturn(testSubscription);

            mockMvc.perform(get("/api/v1/subscriptions/{id}", 1))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numSub", is(1)))
                    .andExpect(jsonPath("$.price", is(100.0)))
                    .andExpect(jsonPath("$.typeSub", is("MONTHLY")));

            verify(subscriptionServices).retrieveSubscriptionById(1L);
        }

        @Test
        @DisplayName("Should return 404 when subscription not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(subscriptionServices.retrieveSubscriptionById(999L))
                    .thenReturn(null);

            mockMvc.perform(get("/api/v1/subscriptions/{id}", 999))
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(subscriptionServices).retrieveSubscriptionById(999L);
        }

        @Test
        @DisplayName("Should get subscriptions by type")
        void shouldGetSubscriptionsByType() throws Exception {
            Set<Subscription> subscriptions = new HashSet<>(Collections.singletonList(testSubscription));
            when(subscriptionServices.getSubscriptionByType(TypeSubscription.MONTHLY))
                    .thenReturn(subscriptions);

            mockMvc.perform(get("/api/v1/subscriptions/by-type/{type}", "MONTHLY"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].typeSub", is("MONTHLY")))
                    .andExpect(jsonPath("$[0].price", is(100.0)));

            verify(subscriptionServices).getSubscriptionByType(TypeSubscription.MONTHLY);
        }

        @Test
        @DisplayName("Should get subscriptions by date range")
        void shouldGetSubscriptionsByDateRange() throws Exception {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now().plusMonths(1);
            when(subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate))
                    .thenReturn(Arrays.asList(testSubscription));

            mockMvc.perform(get("/api/v1/subscriptions/by-date-range")
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].typeSub", is("MONTHLY")));

            verify(subscriptionServices).retrieveSubscriptionsByDates(startDate, endDate);
        }

        @Test
        @DisplayName("Should reject invalid date range")
        void shouldRejectInvalidDateRange() throws Exception {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.minusDays(1); // End date before start date

            mockMvc.perform(get("/api/v1/subscriptions/by-date-range")
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(subscriptionServices, never()).retrieveSubscriptionsByDates(any(), any());
        }
    }

    @Nested
    @DisplayName("Update Subscription Tests")
    class UpdateSubscriptionTests {

        @Test
        @DisplayName("Should update subscription successfully")
        void shouldUpdateSubscription() throws Exception {
            when(subscriptionServices.updateSubscription(any(Subscription.class)))
                    .thenReturn(testSubscription);

            mockMvc.perform(put("/api/v1/subscriptions/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSubscription)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numSub", is(1)))
                    .andExpect(jsonPath("$.price", is(100.0)));

            verify(subscriptionServices).updateSubscription(any(Subscription.class));
        }

        @Test
        @DisplayName("Should reject update with mismatched IDs")
        void shouldRejectUpdateWithMismatchedIds() throws Exception {
            mockMvc.perform(put("/api/v1/subscriptions/{id}", 2)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSubscription)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(subscriptionServices, never()).updateSubscription(any());
        }
    }

    @Nested
    @DisplayName("Delete Subscription Tests")
    class DeleteSubscriptionTests {

        @Test
        @DisplayName("Should delete existing subscription")
        void shouldDeleteExistingSubscription() throws Exception {
            when(subscriptionServices.retrieveSubscriptionById(1L))
                    .thenReturn(testSubscription);

            mockMvc.perform(delete("/api/v1/subscriptions/{id}", 1))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            verify(subscriptionServices).retrieveSubscriptionById(1L);
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent subscription")
        void shouldReturn404WhenDeletingNonExistent() throws Exception {
            when(subscriptionServices.retrieveSubscriptionById(999L))
                    .thenReturn(null);

            mockMvc.perform(delete("/api/v1/subscriptions/{id}", 999))
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(subscriptionServices).retrieveSubscriptionById(999L);
        }
    }
}