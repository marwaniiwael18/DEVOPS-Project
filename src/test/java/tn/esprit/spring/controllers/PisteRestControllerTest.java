package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.config.TestConfig;
import tn.esprit.spring.dto.PisteDTO;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.mappers.PisteMapper;
import tn.esprit.spring.services.IPisteServices;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PisteRestController.class)
@Import(TestConfig.class)
public class PisteRestControllerTest {

    // API endpoint constants
    private static final String API_PISTE_ADD = "/piste/add";
    private static final String API_PISTE_ALL = "/piste/all";
    private static final String API_PISTE_GET = "/piste/get/{id}";
    private static final String API_PISTE_DELETE = "/piste/delete/{id}";
    
    // Test data constants
    private static final String TEST_PISTE_NAME = "Test Piste";
    private static final String PISTE_1_NAME = "Piste 1";
    private static final String PISTE_2_NAME = "Piste 2";
    
    // JSON path constants
    private static final String JSON_PATH_ID = "$.id";
    private static final String JSON_PATH_NAME = "$.name";
    private static final String JSON_PATH_COLOR = "$.color";
    private static final String JSON_PATH_LENGTH = "$.length()";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPisteServices pisteServices;

    @Autowired
    private PisteMapper pisteMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Piste piste;
    private PisteDTO pisteDTO;

    @BeforeEach
    void setUp() {
        // Create sample piste and convert to DTO
        piste = createSamplePiste(1L, TEST_PISTE_NAME, Color.BLUE, 500, 15);
        pisteDTO = pisteMapper.toDTO(piste);
    }

    @Test
    public void testAddPiste() throws Exception {
        when(pisteServices.addPiste(any(Piste.class))).thenReturn(piste);

        mockMvc.perform(post(API_PISTE_ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pisteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(piste.getNumPiste()));

        verify(pisteServices, times(1)).addPiste(any(Piste.class));
    }

    @Test
    public void testGetAllPistes() throws Exception {
        List<Piste> pistes = Arrays.asList(
                createSamplePiste(1L, PISTE_1_NAME, Color.BLUE, 500, 15),
                createSamplePiste(2L, PISTE_2_NAME, Color.RED, 700, 20)
        );

        when(pisteServices.retrieveAllPistes()).thenReturn(pistes);

        mockMvc.perform(get(API_PISTE_ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_LENGTH).value(2));

        verify(pisteServices, times(1)).retrieveAllPistes();
    }

    @Test
    public void testGetPisteById() throws Exception {
        when(pisteServices.retrievePiste(1L)).thenReturn(piste);

        mockMvc.perform(get(API_PISTE_GET, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID, is(1)))
                .andExpect(jsonPath(JSON_PATH_NAME, is(TEST_PISTE_NAME)))
                .andExpect(jsonPath(JSON_PATH_COLOR, is(Color.BLUE.toString())));
                
        verify(pisteServices, times(1)).retrievePiste(1L);
    }

    @Test
    public void testDeletePiste() throws Exception {
        doNothing().when(pisteServices).removePiste(anyLong());

        mockMvc.perform(delete(API_PISTE_DELETE, 1))
                .andExpect(status().isOk());

        verify(pisteServices, times(1)).removePiste(anyLong());
    }

    // Utility method to create a sample Piste
    private Piste createSamplePiste(Long id, String name, Color color, Integer length, Integer slope) {
        Piste newPiste = new Piste();
        newPiste.setNumPiste(id);
        newPiste.setNamePiste(name);
        newPiste.setColor(color);
        newPiste.setLength(length);
        newPiste.setSlope(slope);
        return newPiste;
    }
}
