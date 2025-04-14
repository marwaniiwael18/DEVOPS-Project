package tn.esprit.spring.dto;

import org.junit.jupiter.api.Test;

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

public class InstructorDTOTest {

    private final Validator validator;

    public InstructorDTOTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testValidInstructorDTO() {
        InstructorDTO dto = new InstructorDTO("John", "Doe", LocalDate.now(), "New York", "john.doe@example.com");
        Set<ConstraintViolation<InstructorDTO>> violations = validator.validate(dto);
        assertEquals(0, violations.size());
    }

    @Test
    void testInvalidInstructorDTO() {
        InstructorDTO dto = new InstructorDTO("", "", null, null, "invalid-email");
        Set<ConstraintViolation<InstructorDTO>> violations = validator.validate(dto);
        assertEquals(5, violations.size()); // Updated to expect 5 violations
    }

    @Test
    void testEqualsAndHashCode() {
        InstructorDTO dto1 = new InstructorDTO("John", "Doe", LocalDate.now(), "New York", "john.doe@example.com");
        InstructorDTO dto2 = new InstructorDTO("John", "Doe", LocalDate.now(), "New York", "john.doe@example.com");
        InstructorDTO dto3 = new InstructorDTO("Jane", "Smith", LocalDate.now(), "Los Angeles", "jane.smith@example.com");

        // Test equality
        assertEquals(dto1, dto2); // Should pass now
        assertNotEquals(dto1, dto3);

        // Test hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode()); // Should pass now
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        InstructorDTO dto = new InstructorDTO("John", "Doe", LocalDate.now(), "New York", "john.doe@example.com");
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("InstructorDTO"));
    }
}
