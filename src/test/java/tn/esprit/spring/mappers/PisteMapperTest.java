package tn.esprit.spring.mappers;

import org.junit.jupiter.api.Test;
import tn.esprit.spring.dto.PisteDTO;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;

import static org.junit.jupiter.api.Assertions.*;

class PisteMapperTest {

    private final PisteMapper mapper = new PisteMapper();

    @Test
    void testToDTO() {
        Piste piste = new Piste();
        piste.setNumPiste(1L);
        piste.setNamePiste("Blue Trail");
        piste.setColor(Color.BLUE); // Use Color enum
        piste.setLength(3000);
        piste.setSlope(30);

        PisteDTO dto = mapper.toDTO(piste);

        assertNotNull(dto);
        assertEquals(piste.getNumPiste(), dto.getId());
        assertEquals(piste.getNamePiste(), dto.getName());
        assertEquals(piste.getColor(), dto.getColor());
        assertEquals(piste.getLength(), dto.getLength());
        assertEquals(piste.getSlope(), dto.getSlope());
    }

    @Test
    void testToEntity() {
        PisteDTO dto = new PisteDTO();
        dto.setId(1L);
        dto.setName("Blue Trail");
        dto.setColor(Color.BLUE); // Use Color enum
        dto.setLength(3000);
        dto.setSlope(30);

        Piste piste = mapper.toEntity(dto);

        assertNotNull(piste);
        assertEquals(dto.getId(), piste.getNumPiste());
        assertEquals(dto.getName(), piste.getNamePiste());
        assertEquals(dto.getColor(), piste.getColor());
        assertEquals(dto.getLength(), piste.getLength());
        assertEquals(dto.getSlope(), piste.getSlope());
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
