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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class PisteServiceTest {

    @Mock
    private IPisteRepository pisteRepository;

    @InjectMocks
    private PisteServicesImpl pisteServices;

    private Piste piste;

    @BeforeEach
    void setUp() {
        piste = new Piste();
        piste.setNumPiste(1L);
        piste.setNamePiste("Piste 1");
        piste.setColor(Color.RED);
        piste.setLength(1000);
        piste.setSlope(30);
    }

    @Test
    void testRemovePiste() {
        Piste piste = new Piste();
        piste.setNumPiste(1L);

        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));

        pisteServices.removePiste(1L);

        // Vérifie que delete() est bien appelé sur l'entité, pas sur l'ID
        verify(pisteRepository, times(1)).delete(piste); // Correct, vous appelez delete sur l'objet, pas deleteById
    }

    @Test
    void testAddPiste() {
        Piste piste = new Piste();
        piste.setNumPiste(1L);

        when(pisteRepository.save(any(Piste.class))).thenReturn(piste);

        // Ton test pour ajouter la piste
        assertEquals(piste, pisteServices.addPiste(piste));

        // Vérifie que save() a bien été appelé
        verify(pisteRepository, times(1)).save(any(Piste.class));
    }
}
