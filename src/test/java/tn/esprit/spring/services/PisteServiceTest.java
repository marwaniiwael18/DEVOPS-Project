package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.repositories.IPisteRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PisteServiceTest {

    @Mock
    private IPisteRepository pisteRepository;

    @InjectMocks
    private PisteServicesImpl pisteService;

    private Piste piste;

    @BeforeEach
    void setUp() {
        piste = new Piste();
        piste.setNumPiste(1L);
        piste.setNamePiste("Blue Piste");
        piste.setColor(Color.BLUE);
        piste.setLength(600);
        piste.setSlope(15);
    }

    @Test
    void testAddPiste() {
        when(pisteRepository.save(any(Piste.class))).thenReturn(piste);

        Piste result = pisteService.addPiste(piste);

        assertNotNull(result);
        assertEquals(1L, result.getNumPiste());
        verify(pisteRepository, times(1)).save(any(Piste.class));
    }

    @Test
    void testRetrievePisteByIdFound() {
        when(pisteRepository.findById(1L)).thenReturn(java.util.Optional.of(piste));

        Piste result = pisteService.retrievePiste(1L);

        assertNotNull(result);
        assertEquals(1L, result.getNumPiste());
        verify(pisteRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrievePisteByIdNotFound() {
        when(pisteRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        Piste result = pisteService.retrievePiste(99L);

        assertNull(result);
        verify(pisteRepository, times(1)).findById(99L);
    }
}
