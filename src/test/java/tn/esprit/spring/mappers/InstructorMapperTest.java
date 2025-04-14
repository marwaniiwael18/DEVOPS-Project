package tn.esprit.spring.mappers;

import org.junit.jupiter.api.Test;
import tn.esprit.spring.dto.InstructorDTO;
import tn.esprit.spring.entities.Instructor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InstructorMapperTest {

    private final InstructorMapper mapper = new InstructorMapper();

    @Test
    void testToDTO() {
        Instructor instructor = new Instructor();
        instructor.setFirstName("John");
        instructor.setLastName("Doe");
        instructor.setDateOfHire(LocalDate.now());
        instructor.setEmail("john.doe@example.com");

        InstructorDTO dto = mapper.toDTO(instructor);

        assertNotNull(dto);
        assertEquals(instructor.getFirstName(), dto.getFirstName());
        assertEquals(instructor.getLastName(), dto.getLastName());
        assertEquals(instructor.getDateOfHire(), dto.getDateOfHire());
        assertEquals(instructor.getEmail(), dto.getEmail());
    }

    @Test
    void testToEntity() {
        InstructorDTO dto = new InstructorDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDateOfHire(LocalDate.now());
        dto.setEmail("john.doe@example.com");

        Instructor instructor = mapper.toEntity(dto);

        assertNotNull(instructor);
        assertEquals(dto.getFirstName(), instructor.getFirstName());
        assertEquals(dto.getLastName(), instructor.getLastName());
        assertEquals(dto.getDateOfHire(), instructor.getDateOfHire());
        assertEquals(dto.getEmail(), instructor.getEmail());
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
