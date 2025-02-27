package tn.esprit.spring.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.controllers.PisteRestController;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.services.IPisteServices;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PisteRestController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class PisteRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPisteServices pisteServices;

    @Autowired
    private ObjectMapper objectMapper;

    private Piste piste;

    private static final String PISTE_ADD_ENDPOINT = "/piste/add";
    private static final String PISTE_GET_ENDPOINT = "/piste/get/{id-piste}";

    @BeforeEach
    void setUp() {
        piste = new Piste();
        piste.setNumPiste(1L);
        piste.setNamePiste("Green Piste");
        piste.setColor(Color.GREEN);
        piste.setLength(500);
        piste.setSlope(10);
    }

    @Test
    void testAddPisteSuccess() throws Exception {
        when(pisteServices.addPiste(any(Piste.class))).thenReturn(piste);

        mockMvc.perform(post(PISTE_ADD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(piste)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numPiste").value(piste.getNumPiste()));

        verify(pisteServices, times(1)).addPiste(any(Piste.class));
    }

    @Test
    void testGetPisteByIdSuccess() throws Exception {
        when(pisteServices.retrievePiste(1L)).thenReturn(piste);

        mockMvc.perform(get(PISTE_GET_ENDPOINT, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numPiste").value(1L));

        verify(pisteServices, times(1)).retrievePiste(1L);
    }
}
