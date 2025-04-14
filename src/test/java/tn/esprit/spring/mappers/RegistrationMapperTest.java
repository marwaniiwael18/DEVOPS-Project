package tn.esprit.spring.mappers;

import org.junit.jupiter.api.Test;
import tn.esprit.spring.dto.RegistrationDTO;
import tn.esprit.spring.entities.Registration;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationMapperTest {

    private final RegistrationMapper mapper = new RegistrationMapper();

    @Test
    void testToDTO() {
        Registration registration = new Registration();
        registration.setNumRegistration(1L);
        registration.setNumWeek(5);

        RegistrationDTO dto = mapper.toDTO(registration);

        assertNotNull(dto);
        assertEquals(registration.getNumRegistration(), dto.getId());
        assertEquals(registration.getNumWeek(), dto.getNumWeek());
    }

    @Test
    void testToEntity() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(1L);
        dto.setNumWeek(5);

        Registration registration = mapper.toEntity(dto);

        assertNotNull(registration);
        assertEquals(dto.getId(), registration.getNumRegistration());
        assertEquals(dto.getNumWeek(), registration.getNumWeek());
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
