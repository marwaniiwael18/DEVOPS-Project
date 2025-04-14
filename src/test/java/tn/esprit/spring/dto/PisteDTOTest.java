package tn.esprit.spring.dto;

import org.junit.jupiter.api.Test;
import tn.esprit.spring.entities.Color;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PisteDTOTest {

    private final Validator validator;

    public PisteDTOTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testValidPisteDTO() {
        PisteDTO dto = new PisteDTO(1L, "Piste A", Color.RED, 1000, 30);
        Set<ConstraintViolation<PisteDTO>> violations = validator.validate(dto);
        assertEquals(0, violations.size());
    }

    @Test
    void testInvalidPisteDTO() {
        PisteDTO dto = new PisteDTO(null, "", null, -1, -1);
        Set<ConstraintViolation<PisteDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size());
    }

    @Test
    void testEqualsAndHashCode() {
        PisteDTO dto1 = new PisteDTO(1L, "Piste A", Color.RED, 1000, 30);
        PisteDTO dto2 = new PisteDTO(1L, "Piste A", Color.RED, 1000, 30);
        PisteDTO dto3 = new PisteDTO(2L, "Piste B", Color.BLUE, 800, 25);

        // Test equality
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);

        // Test hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        PisteDTO dto = new PisteDTO(1L, "Piste A", Color.RED, 1000, 30);
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("PisteDTO"));
    }
}
