package tn.esprit.spring.services;

import org.junit.jupiter.api.*;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Subscription Services Tests")
class SubscriptionServicesImplTest {

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @Mock
    private ISkierRepository skierRepository;

    @InjectMocks
    private SubscriptionServicesImpl subscriptionService;

    private Subscription monthlySubscription;
    private Subscription annualSubscription;
    private Skier skier;

    @BeforeEach
    void setUp() {
        // Initialize monthly subscription
        monthlySubscription = new Subscription();
        monthlySubscription.setNumSub(1L);
        monthlySubscription.setStartDate(LocalDate.now());
        monthlySubscription.setEndDate(LocalDate.now().plusMonths(1));
        monthlySubscription.setPrice(100.0f);
        monthlySubscription.setTypeSub(TypeSubscription.MONTHLY);

        // Initialize annual subscription
        annualSubscription = new Subscription();
        annualSubscription.setNumSub(2L);
        annualSubscription.setStartDate(LocalDate.now());
        annualSubscription.setEndDate(LocalDate.now().plusYears(1));
        annualSubscription.setPrice(1000.0f);
        annualSubscription.setTypeSub(TypeSubscription.ANNUAL);

        // Initialize skier
        skier = new Skier();
        skier.setNumSkier(1L);
        skier.setSubscription(monthlySubscription);
    }

    @Nested
    @DisplayName("Add Subscription Tests")
    class AddSubscriptionTests {

        @Test
        @DisplayName("Should add monthly subscription with correct end date")
        void shouldAddMonthlySubscriptionWithCorrectEndDate() {
            // Arrange
            Subscription newSubscription = new Subscription();
            newSubscription.setStartDate(LocalDate.now());
            newSubscription.setTypeSub(TypeSubscription.MONTHLY);
            when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            Subscription result = subscriptionService.addSubscription(newSubscription);

            // Assert
            assertAll(
                    "Validate monthly subscription",
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(LocalDate.now().plusMonths(1), result.getEndDate(), "End date should be 1 month after start date"),
                    () -> assertEquals(TypeSubscription.MONTHLY, result.getTypeSub(), "Subscription type should be MONTHLY")
            );
            verify(subscriptionRepository).save(any(Subscription.class));
        }

        @Test
        @DisplayName("Should handle null subscription")
        void shouldHandleNullSubscription() {
            // Act & Assert
            assertThrows(NullPointerException.class,
                    () -> subscriptionService.addSubscription(null),
                    "Should throw NullPointerException when subscription is null"
            );
            verify(subscriptionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update Subscription Tests")
    class UpdateSubscriptionTests {

        @Test
        @DisplayName("Should update existing subscription")
        void shouldUpdateExistingSubscription() {
            // Arrange
            monthlySubscription.setPrice(150.0f);
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(monthlySubscription);

            // Act
            Subscription result = subscriptionService.updateSubscription(monthlySubscription);

            // Assert
            assertAll(
                    "Validate updated subscription",
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(150.0f, result.getPrice(), "Price should be updated"),
                    () -> assertEquals(monthlySubscription.getNumSub(), result.getNumSub(), "Subscription ID should remain same")
            );
            verify(subscriptionRepository).save(monthlySubscription);
        }
    }

    @Nested
    @DisplayName("Retrieve Subscription Tests")
    class RetrieveSubscriptionTests {

        @Test
        @DisplayName("Should retrieve subscriptions ordered by end date")
        void shouldRetrieveSubscriptionsOrderedByEndDate() {
            // Arrange
            List<Subscription> subscriptions = Arrays.asList(monthlySubscription, annualSubscription);
            when(subscriptionRepository.findDistinctOrderByEndDateAsc()).thenReturn(subscriptions);
            when(skierRepository.findBySubscription(any(Subscription.class))).thenReturn(skier);

            // Act
            subscriptionService.retrieveSubscriptions();

            // Assert
            verify(subscriptionRepository).findDistinctOrderByEndDateAsc();
            verify(skierRepository, times(2)).findBySubscription(any(Subscription.class));
        }

        @Test
        @DisplayName("Should handle empty subscription list")
        void shouldHandleEmptySubscriptionList() {
            // Arrange
            when(subscriptionRepository.findDistinctOrderByEndDateAsc()).thenReturn(Collections.emptyList());

            // Act
            subscriptionService.retrieveSubscriptions();

            // Assert
            verify(subscriptionRepository).findDistinctOrderByEndDateAsc();
            verify(skierRepository, never()).findBySubscription(any(Subscription.class));
        }
    }

    @Test
    @DisplayName("Should retrieve subscription by type")
    void shouldRetrieveSubscriptionsByType() {
        // Arrange
        Set<Subscription> monthlySubscriptions = Set.of(monthlySubscription);
        when(subscriptionRepository.findByTypeSubOrderByStartDateAsc(TypeSubscription.MONTHLY))
                .thenReturn(monthlySubscriptions);

        // Act
        Set<Subscription> result = subscriptionService.getSubscriptionByType(TypeSubscription.MONTHLY);

        // Assert
        assertAll(
                "Validate subscriptions by type",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(), "Should return correct number of subscriptions"),
                () -> assertTrue(result.contains(monthlySubscription), "Should contain the monthly subscription")
        );
        verify(subscriptionRepository).findByTypeSubOrderByStartDateAsc(TypeSubscription.MONTHLY);
    }
}