package tn.esprit.spring.dto;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrationDTOTest {

    private final Validator validator;

    public RegistrationDTOTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testValidRegistrationDTO() {
        RegistrationDTO dto = new RegistrationDTO(1L, 10, 1L, 1L, 1L);
        Set<ConstraintViolation<RegistrationDTO>> violations = validator.validate(dto);
        assertEquals(0, violations.size());
    }

    @Test
    void testInvalidRegistrationDTO() {
        RegistrationDTO dto = new RegistrationDTO(null, -1, null, null, null);
        Set<ConstraintViolation<RegistrationDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size());
    }

    @Test
    void testEqualsAndHashCode() {
        RegistrationDTO dto1 = new RegistrationDTO(1L, 10, 1L, 1L, 1L);
        RegistrationDTO dto2 = new RegistrationDTO(1L, 10, 1L, 1L, 1L);
        RegistrationDTO dto3 = new RegistrationDTO(2L, 20, 2L, 2L, 2L);

        // Test equality
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);

        // Test hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        RegistrationDTO dto = new RegistrationDTO(1L, 10, 1L, 1L, 1L);
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("RegistrationDTO"));
    }
}
