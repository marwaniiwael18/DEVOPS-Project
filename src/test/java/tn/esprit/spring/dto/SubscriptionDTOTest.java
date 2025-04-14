package tn.esprit.spring.dto;

import org.junit.jupiter.api.Test;
import tn.esprit.spring.entities.TypeSubscription;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubscriptionDTOTest {

    private final Validator validator;

    public SubscriptionDTOTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testValidSubscriptionDTO() {
        SubscriptionDTO dto = new SubscriptionDTO(LocalDate.now(), 100.0f, TypeSubscription.ANNUAL, 1L, LocalDate.now().plusDays(30));
        Set<ConstraintViolation<SubscriptionDTO>> violations = validator.validate(dto);
        assertEquals(0, violations.size());
    }

    @Test
    void testInvalidSubscriptionDTO() {
        SubscriptionDTO dto = new SubscriptionDTO(null, -100.0f, null, null, null);
        Set<ConstraintViolation<SubscriptionDTO>> violations = validator.validate(dto);
        assertEquals(3, violations.size());
    }

    @Test
    void testEqualsAndHashCode() {
        SubscriptionDTO dto1 = new SubscriptionDTO(LocalDate.now(), 100.0f, TypeSubscription.ANNUAL, 1L, LocalDate.now().plusDays(30));
        SubscriptionDTO dto2 = new SubscriptionDTO(LocalDate.now(), 100.0f, TypeSubscription.ANNUAL, 1L, LocalDate.now().plusDays(30));
        SubscriptionDTO dto3 = new SubscriptionDTO(LocalDate.now(), 200.0f, TypeSubscription.MONTHLY, 2L, LocalDate.now().plusDays(15));

        // Test equality
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);

        // Test hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        SubscriptionDTO dto = new SubscriptionDTO(LocalDate.now(), 100.0f, TypeSubscription.ANNUAL, 1L, LocalDate.now().plusDays(30));
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("SubscriptionDTO"));
    }
}
