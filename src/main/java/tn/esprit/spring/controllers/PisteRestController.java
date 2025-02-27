package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.services.IPisteServices;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Tag(name = "\uD83C\uDFBF Piste Management")
@RestController
@RequestMapping("/piste")
@RequiredArgsConstructor
public class PisteRestController {

    private final IPisteServices pisteServices;

    @Operation(description = "Add Piste")
    @PostMapping("/add")
    public Piste addPiste(@RequestBody Piste piste){
        return  pisteServices.addPiste(piste);
    }

    @Operation(description = "Retrieve all Pistes")
    @GetMapping("/all")
    public List<Piste> getAllPistes(){
        return pisteServices.retrieveAllPistes();
    }

    @Operation(description = "Retrieve Piste by Id")
    @GetMapping("/get/{id-piste}")
    public ResponseEntity<Piste> getById(@PathVariable("id-piste") Long numPiste){
        Piste piste = pisteServices.retrievePiste(numPiste);
        if (piste == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(piste);
    }

    @Operation(description = "Delete Piste by Id")
    @DeleteMapping("/delete/{id-piste}")
    public ResponseEntity<Void> deleteById(@PathVariable("id-piste") Long numPiste){
        if (pisteServices.retrievePiste(numPiste) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        pisteServices.removePiste(numPiste);
        return ResponseEntity.noContent().build();  // Renvoie un code 204 en cas de succès
    }
}
