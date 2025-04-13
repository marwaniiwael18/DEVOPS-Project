package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkierServiceImplTest {

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @Mock
    private IPisteRepository pisteRepository;

    @InjectMocks
    private SkierServicesImpl skierService;

    @BeforeEach
    void setUp() {
        // Mockito annotations are handled by @ExtendWith, so no need to call initMocks explicitly.
    }

    private Skier createSkier(Long numSkier, String firstName, String lastName, Subscription subscription) {
        Skier skier = new Skier(numSkier, firstName, lastName);
        skier.setSubscription(subscription);
        return skier;
    }

    @Test
    void testRetrieveAllSkiers() {
        when(skierRepository.findAll()).thenReturn(Arrays.asList(new Skier(1L, "John", "Doe")));

        List<Skier> retrievedSkiers = skierService.retrieveAllSkiers();

        assertNotNull(retrievedSkiers);
        assertEquals(1, retrievedSkiers.size());
        verify(skierRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveSkierFound() {
        Skier skier = createSkier(1L, "Jane", "Doe", new Subscription(1L, TypeSubscription.ANNUAL, LocalDate.now(), null));
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));

        Skier retrievedSkier = skierService.retrieveSkier(1L);

        assertNotNull(retrievedSkier);
        assertEquals(1L, retrievedSkier.getNumSkier());
        verify(skierRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveSkierNotFound() {
        when(skierRepository.findById(anyLong())).thenReturn(Optional.empty());

        Skier retrievedSkier = skierService.retrieveSkier(99L);

        assertNull(retrievedSkier);
        verify(skierRepository, times(1)).findById(99L);
    }

    @Test
    void testAddSkierWithSubscription() {
        Subscription subscription = new Subscription(1L, TypeSubscription.ANNUAL, LocalDate.now(), null);
        Skier skier = new Skier(1L, "Michael", "Smith", subscription);

        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Skier result = skierService.addSkier(skier);

        assertNotNull(result);
        assertEquals(TypeSubscription.ANNUAL, result.getSubscription().getTypeSub());
        assertEquals(subscription.getStartDate().plusYears(1), result.getSubscription().getEndDate());
        verify(skierRepository, times(1)).save(any(Skier.class));
    }

    @Test
    void testAssignSkierToSubscription() {
        Skier skier = createSkier(1L, "Emma", "Brown", null);
        Subscription subscription = new Subscription(1L, TypeSubscription.MONTHLY, LocalDate.now(), null);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Skier result = skierService.assignSkierToSubscription(1L, 1L);

        assertNotNull(result);
        assertEquals(subscription, result.getSubscription());
        verify(skierRepository, times(1)).findById(1L);
        verify(subscriptionRepository, times(1)).findById(1L);
        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    void testRemoveSkier() {
        Long skierId = 1L;
        doNothing().when(skierRepository).deleteById(skierId);

        skierService.removeSkier(skierId);

        verify(skierRepository, times(1)).deleteById(skierId);
    }

    @Test
    void testAssignSkierToPiste() {
        Skier skier = createSkier(1L, "Lucas", "Johnson", null);
        Piste piste = new Piste(1L, "Blue", 5);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Skier result = skierService.assignSkierToPiste(1L, 1L);

        assertNotNull(result);
        assertTrue(result.getPistes().contains(piste));
        verify(skierRepository, times(1)).findById(1L);
        verify(pisteRepository, times(1)).findById(1L);
        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    void testRetrieveSkiersBySubscriptionType() {
        List<Skier> skiers = Arrays.asList(new Skier(1L, "Liam", "White", new Subscription(1L, TypeSubscription.ANNUAL, LocalDate.now(), null)),
                new Skier(2L, "Sophia", "Black", new Subscription(2L, TypeSubscription.ANNUAL, LocalDate.now(), null)));
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.ANNUAL)).thenReturn(skiers);

        List<Skier> result = skierService.retrieveSkiersBySubscriptionType(TypeSubscription.ANNUAL);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(skierRepository, times(1)).findBySubscription_TypeSub(TypeSubscription.ANNUAL);
    }

    @Test
    void testAssignSkierToSubscription_SkierNotFound() {
        when(skierRepository.findById(1L)).thenReturn(Optional.empty());
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(new Subscription(1L, TypeSubscription.MONTHLY, LocalDate.now(), null)));

        Skier result = skierService.assignSkierToSubscription(1L, 1L);

        assertNull(result);
        verify(skierRepository, times(1)).findById(1L);
        verify(subscriptionRepository, times(1)).findById(1L);
    }

    @Test
    void testAssignSkierToSubscription_SubscriptionNotFound() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(new Skier(1L, "John", "Doe")));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        Skier result = skierService.assignSkierToSubscription(1L, 1L);

        assertNull(result);
        verify(skierRepository, times(1)).findById(1L);
        verify(subscriptionRepository, times(1)).findById(1L);
    }

    @Test
    void testAssignSkierToSubscription_Success() {
        Skier skier = new Skier(1L, "John", "Doe");
        Subscription subscription = new Subscription(1L, TypeSubscription.MONTHLY, LocalDate.now(), null);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Skier result = skierService.assignSkierToSubscription(1L, 1L);

        assertNotNull(result);
        assertEquals(subscription, result.getSubscription());
        verify(skierRepository, times(1)).findById(1L);
        verify(subscriptionRepository, times(1)).findById(1L);
        verify(skierRepository, times(1)).save(skier);
    }
}