package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.repositories.ISubscriptionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServicesImplTest {

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionServicesImpl subscriptionService;

    private Subscription subscription;

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
    void testAddSubscription() {
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        Subscription result = subscriptionService.addSubscription(subscription);

        assertNotNull(result);
        assertEquals(1L, result.getNumSub());
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    void testUpdateSubscription() {
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        Subscription result = subscriptionService.updateSubscription(subscription);

        assertNotNull(result);
        assertEquals(subscription.getNumSub(), result.getNumSub());
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    void testRetrieveSubscriptionByIdFound() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        Subscription result = subscriptionService.retrieveSubscriptionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getNumSub());
        verify(subscriptionRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveSubscriptionByIdNotFound() {
        when(subscriptionRepository.findById(99L)).thenReturn(Optional.empty());

        Subscription result = subscriptionService.retrieveSubscriptionById(99L);

        assertNull(result);
        verify(subscriptionRepository, times(1)).findById(99L);
    }

    @Test
    void testRetrieveSubscriptions() {
        List<Subscription> mockedList = List.of(subscription);
        when(subscriptionRepository.findDistinctOrderByEndDateAsc()).thenReturn(mockedList);

        subscriptionService.retrieveSubscriptions();

        verify(subscriptionRepository, times(1)).findDistinctOrderByEndDateAsc();
    }
}
