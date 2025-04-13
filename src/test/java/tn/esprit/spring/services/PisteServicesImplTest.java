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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PisteServicesImplTest {
    private static final String PISTE_A_NAME = "Piste A";
    
    @Mock
    private IPisteRepository pisteRepository;
    
    @InjectMocks
    private PisteServicesImpl pisteServices;
    
    private Piste piste1;
    private Piste piste2;
    
    @BeforeEach
    public void setup() {
        piste1 = new Piste();
        piste1.setNumPiste(1L);
        piste1.setNamePiste(PISTE_A_NAME);
        piste1.setLength(1000);
        piste1.setSlope(20);
        piste1.setColor(Color.RED);
        
        piste2 = new Piste();
        piste2.setNumPiste(2L);
        piste2.setNamePiste("Piste B");
        piste2.setLength(1500);
        piste2.setSlope(30);
        piste2.setColor(Color.BLUE);
    }
    
    @Test
    public void testRetrieveAllPistes() {
        // Given
        List<Piste> pistes = Arrays.asList(piste1, piste2);
        when(pisteRepository.findAll()).thenReturn(pistes);
        
        // When
        List<Piste> result = pisteServices.retrieveAllPistes();
        
        // Then
        assertEquals(2, result.size());
        assertEquals(PISTE_A_NAME, result.get(0).getNamePiste());
        assertEquals("Piste B", result.get(1).getNamePiste());
        verify(pisteRepository, times(1)).findAll();
    }
    
    @Test
    public void testAddPiste() {
        // Given
        when(pisteRepository.save(piste1)).thenReturn(piste1);
        
        // When
        Piste savedPiste = pisteServices.addPiste(piste1);
        
        // Then
        assertNotNull(savedPiste);
        assertEquals(1L, savedPiste.getNumPiste());
        assertEquals(PISTE_A_NAME, savedPiste.getNamePiste());
        assertEquals(1000, savedPiste.getLength());
        assertEquals(20, savedPiste.getSlope());
        assertEquals(Color.RED, savedPiste.getColor());
        verify(pisteRepository, times(1)).save(piste1);
    }
    
    @Test
    public void testRemovePiste() {
        // Given
        Long numPiste = 1L;
        doNothing().when(pisteRepository).deleteById(numPiste);
        
        // When
        pisteServices.removePiste(numPiste);
        
        // Then
        verify(pisteRepository, times(1)).deleteById(numPiste);
    }
    
    @Test
    public void testRetrievePisteFound() {
        // Given
        Long numPiste = 1L;
        when(pisteRepository.findById(numPiste)).thenReturn(Optional.of(piste1));
        
        // When
        Piste foundPiste = pisteServices.retrievePiste(numPiste);
        
        // Then
        assertNotNull(foundPiste);
        assertEquals(PISTE_A_NAME, foundPiste.getNamePiste());
        assertEquals(Color.RED, foundPiste.getColor());
        verify(pisteRepository, times(1)).findById(numPiste);
    }
    
    @Test
    public void testRetrievePisteNotFound() {
        // Given
        Long numPiste = 3L;
        when(pisteRepository.findById(numPiste)).thenReturn(Optional.empty());
        
        // When
        Piste foundPiste = pisteServices.retrievePiste(numPiste);
        
        // Then
        assertNull(foundPiste);
        verify(pisteRepository, times(1)).findById(numPiste);
    }
}
