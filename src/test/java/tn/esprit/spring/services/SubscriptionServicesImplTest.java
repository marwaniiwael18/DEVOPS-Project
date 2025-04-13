package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.repositories.ISubscriptionRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServicesImplTest {

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @Mock
    private ISkierRepository skierRepository;

    @InjectMocks
    private SubscriptionServicesImpl subscriptionService;

    private Subscription annualSubscription;
    private Subscription monthlySubscription;
    private Subscription semestrialSubscription;
    private Skier skier;
    private LocalDate startDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.now();
        
        annualSubscription = new Subscription();
        annualSubscription.setNumSub(1L);
        annualSubscription.setStartDate(startDate);
        annualSubscription.setTypeSub(TypeSubscription.ANNUAL);

        monthlySubscription = new Subscription();
        monthlySubscription.setNumSub(2L);
        monthlySubscription.setStartDate(startDate);
        monthlySubscription.setTypeSub(TypeSubscription.MONTHLY);

        semestrialSubscription = new Subscription();
        semestrialSubscription.setNumSub(3L);
        semestrialSubscription.setStartDate(startDate);
        semestrialSubscription.setTypeSub(TypeSubscription.SEMESTRIEL);

        skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("John");
        skier.setLastName("Doe");
        skier.setSubscription(annualSubscription);
    }

    @Test
    void testAddSubscriptionAnnual() {
        // Setup
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(annualSubscription);

        // Execute
        Subscription result = subscriptionService.addSubscription(annualSubscription);

        // Verify
        assertNotNull(result);
        assertEquals(startDate.plusYears(1), result.getEndDate());
        verify(subscriptionRepository, times(1)).save(annualSubscription);
    }

    @Test
    void testAddSubscriptionMonthly() {
        // Setup
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(monthlySubscription);

        // Execute
        Subscription result = subscriptionService.addSubscription(monthlySubscription);

        // Verify
        assertNotNull(result);
        assertEquals(startDate.plusMonths(1), result.getEndDate());
        verify(subscriptionRepository, times(1)).save(monthlySubscription);
    }

    @Test
    void testAddSubscriptionSemestrial() {
        // Setup
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(semestrialSubscription);

        // Execute
        Subscription result = subscriptionService.addSubscription(semestrialSubscription);

        // Verify
        assertNotNull(result);
        assertEquals(startDate.plusMonths(6), result.getEndDate());
        verify(subscriptionRepository, times(1)).save(semestrialSubscription);
    }

    @Test
    void testUpdateSubscription() {
        // Setup
        Subscription updatedSubscription = new Subscription();
        updatedSubscription.setNumSub(1L);
        updatedSubscription.setPrice(200.0f);
        updatedSubscription.setTypeSub(TypeSubscription.ANNUAL);
        
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(updatedSubscription);

        // Execute
        Subscription result = subscriptionService.updateSubscription(updatedSubscription);

        // Verify
        assertNotNull(result);
        assertEquals(200.0f, result.getPrice());
        verify(subscriptionRepository, times(1)).save(updatedSubscription);
    }

    @Test
    void testRetrieveSubscriptionByIdFound() {
        // Setup
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(annualSubscription));

        // Execute
        Subscription result = subscriptionService.retrieveSubscriptionById(1L);

        // Verify
        assertNotNull(result);
        assertEquals(1L, result.getNumSub());
        verify(subscriptionRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveSubscriptionByIdNotFound() {
        // Setup
        when(subscriptionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execute
        Subscription result = subscriptionService.retrieveSubscriptionById(99L);

        // Verify
        assertNull(result);
        verify(subscriptionRepository, times(1)).findById(99L);
    }

    @Test
    void testGetSubscriptionByType() {
        // Setup
        Set<Subscription> annualSubscriptions = new HashSet<>(Arrays.asList(annualSubscription));
        when(subscriptionRepository.findByTypeSubOrderByStartDateAsc(TypeSubscription.ANNUAL)).thenReturn(annualSubscriptions);

        // Execute
        Set<Subscription> result = subscriptionService.getSubscriptionByType(TypeSubscription.ANNUAL);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(subscriptionRepository, times(1)).findByTypeSubOrderByStartDateAsc(TypeSubscription.ANNUAL);
    }

    @Test
    void testRetrieveSubscriptionsByDates() {
        // Setup
        LocalDate end = startDate.plusMonths(1);
        List<Subscription> subscriptions = Arrays.asList(annualSubscription, monthlySubscription);
        when(subscriptionRepository.getSubscriptionsByStartDateBetween(startDate, end)).thenReturn(subscriptions);

        // Execute
        List<Subscription> result = subscriptionService.retrieveSubscriptionsByDates(startDate, end);

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(subscriptionRepository, times(1)).getSubscriptionsByStartDateBetween(startDate, end);
    }

    @Test
    void testRetrieveSubscriptions() {
        // Setup
        // Set the endDate to avoid NullPointerException
        annualSubscription.setEndDate(startDate.plusYears(1));
        
        List<Subscription> subscriptions = Arrays.asList(annualSubscription);
        when(subscriptionRepository.findDistinctOrderByEndDateAsc()).thenReturn(subscriptions);
        when(skierRepository.findBySubscription(any(Subscription.class))).thenReturn(skier);

        // Execute - This is a void method that logs information
        subscriptionService.retrieveSubscriptions();

        // Verify
        verify(subscriptionRepository, times(1)).findDistinctOrderByEndDateAsc();
        verify(skierRepository, times(1)).findBySubscription(any(Subscription.class));
    }

    @Test
    void testShowMonthlyRecurringRevenue() {
        // Setup
        when(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.MONTHLY)).thenReturn(300.0f);
        when(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.SEMESTRIEL)).thenReturn(1200.0f);
        when(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.ANNUAL)).thenReturn(2400.0f);

        // Execute - This is a void method that logs information
        subscriptionService.showMonthlyRecurringRevenue();

        // Verify
        verify(subscriptionRepository, times(1)).recurringRevenueByTypeSubEquals(TypeSubscription.MONTHLY);
        verify(subscriptionRepository, times(1)).recurringRevenueByTypeSubEquals(TypeSubscription.SEMESTRIEL);
        verify(subscriptionRepository, times(1)).recurringRevenueByTypeSubEquals(TypeSubscription.ANNUAL);
    }

    @Test
    void testRetrieveAllSubscriptions() {
        // Setup
        List<Subscription> mockSubscriptions = Arrays.asList(annualSubscription, monthlySubscription, semestrialSubscription);
        when(subscriptionRepository.findAll()).thenReturn(mockSubscriptions);

        // Execute
        List<Subscription> result = subscriptionService.retrieveAllSubscriptions();

        // Verify
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(annualSubscription));
        assertTrue(result.contains(monthlySubscription));
        assertTrue(result.contains(semestrialSubscription));
        verify(subscriptionRepository, times(1)).findAll();
    }
}
