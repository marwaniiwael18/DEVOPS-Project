package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dto.PisteDTO;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.mappers.PisteMapper;
import tn.esprit.spring.services.IPisteServices;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "\uD83C\uDFBF Piste Management")
@RestController
@RequestMapping("/piste")
@RequiredArgsConstructor
public class PisteRestController {

    private final IPisteServices pisteServices;
    private final PisteMapper pisteMapper;

    @Operation(description = "Add Piste")
    @PostMapping("/add")
    public ResponseEntity<PisteDTO> addPiste(@Valid @RequestBody PisteDTO pisteDTO){
        Piste piste = pisteMapper.toEntity(pisteDTO);
        Piste savedPiste = pisteServices.addPiste(piste);
        return ResponseEntity.ok(pisteMapper.toDTO(savedPiste)); // Ensure OK (200) status
    }
    
    @Operation(description = "Retrieve all Pistes")
    @GetMapping("/all")
    public List<PisteDTO> getAllPistes(){
        return pisteServices.retrieveAllPistes().stream()
                .map(pisteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(description = "Retrieve Piste by Id")
    @GetMapping("/get/{id-piste}")
    public ResponseEntity<PisteDTO> getById(@PathVariable("id-piste") Long numPiste){
        Piste piste = pisteServices.retrievePiste(numPiste);
        if (piste == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pisteMapper.toDTO(piste));
    }

    @Operation(description = "Delete Piste by Id")
    @DeleteMapping("/delete/{id-piste}")
    public ResponseEntity<Void> deleteById(@PathVariable("id-piste") Long numPiste){
        pisteServices.removePiste(numPiste);
        return ResponseEntity.ok().build(); // Return 200 OK instead of 204
    }
}
