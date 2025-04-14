package tn.esprit.spring.mappers;

import org.junit.jupiter.api.Test;
import tn.esprit.spring.dto.SubscriptionDTO;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionMapperTest {

    private final SubscriptionMapper mapper = new SubscriptionMapper();

    @Test
    void testToDTO() {
        Subscription subscription = new Subscription();
        subscription.setNumSub(1L);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusDays(10));
        subscription.setPrice(100.0f); // Use Float
        subscription.setTypeSub(TypeSubscription.MONTHLY); // Use TypeSubscription enum

        SubscriptionDTO dto = mapper.toDTO(subscription);

        assertNotNull(dto);
        assertEquals(subscription.getNumSub(), dto.getNumSub());
        assertEquals(subscription.getStartDate(), dto.getStartDate());
        assertEquals(subscription.getEndDate(), dto.getEndDate());
        assertEquals(subscription.getPrice(), dto.getPrice());
        assertEquals(subscription.getTypeSub(), dto.getTypeSub());
    }

    @Test
    void testToEntity() {
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setNumSub(1L);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(10));
        dto.setPrice(100.0f); // Use Float
        dto.setTypeSub(TypeSubscription.MONTHLY); // Use TypeSubscription enum

        Subscription subscription = mapper.toEntity(dto);

        assertNotNull(subscription);
        assertEquals(dto.getNumSub(), subscription.getNumSub());
        assertEquals(dto.getStartDate(), subscription.getStartDate());
        assertEquals(dto.getEndDate(), subscription.getEndDate());
        assertEquals(dto.getPrice(), subscription.getPrice());
        assertEquals(dto.getTypeSub(), subscription.getTypeSub());
    }

    @Test
    void testToDTO_Null() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void testToEntity_Null() {
        assertNull(mapper.toEntity(null));
    }
}
