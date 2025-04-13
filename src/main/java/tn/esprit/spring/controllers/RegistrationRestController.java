package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dto.RegistrationDTO;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.mappers.RegistrationMapper;
import tn.esprit.spring.services.IRegistrationServices;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "\uD83D\uDDD3️Registration Management")
@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationRestController {
    private final IRegistrationServices registrationServices;
    private final RegistrationMapper registrationMapper;

    @Operation(description = "Add Registration and Assign to Skier")
    @PutMapping("/addAndAssignToSkier/{numSkieur}")
    public ResponseEntity<RegistrationDTO> addAndAssignToSkier(@Valid @RequestBody RegistrationDTO registrationDTO,
                                                     @PathVariable("numSkieur") Long numSkieur)
    {
        Registration registration = registrationMapper.toEntity(registrationDTO);
        Registration result = registrationServices.addRegistrationAndAssignToSkier(registration, numSkieur);
        if (result == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(registrationMapper.toDTO(result));
    }
    
    @Operation(description = "Assign Registration to Course")
    @PutMapping("/assignToCourse/{numRegis}/{numCourse}")
    public ResponseEntity<RegistrationDTO> assignToCourse(@PathVariable("numRegis") Long numRegistration,
                                        @PathVariable("numCourse") Long numCourse){
        Registration result = registrationServices.assignRegistrationToCourse(numRegistration, numCourse);
        if (result == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(registrationMapper.toDTO(result));
    }

    @Operation(description = "Add Registration and Assign to Skier and Course")
    @PutMapping("/addAndAssignToSkierAndCourse/{numSkieur}/{numCourse}")
    public ResponseEntity<RegistrationDTO> addAndAssignToSkierAndCourse(@Valid @RequestBody RegistrationDTO registrationDTO,
                                                     @PathVariable("numSkieur") Long numSkieur,
                                                     @PathVariable("numCourse") Long numCourse)
    {
        Registration registration = registrationMapper.toEntity(registrationDTO);
        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, numSkieur, numCourse);
        if (result == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(registrationMapper.toDTO(result));
    }

    @Operation(description = "Numbers of the weeks when an instructor has given lessons in a given support")
    @GetMapping("/numWeeks/{numInstructor}/{support}")
    public ResponseEntity<List<Integer>> numWeeksCourseOfInstructorBySupport(@PathVariable("numInstructor")Long numInstructor,
                                                                  @PathVariable("support") Support support) {
        List<Integer> weeks = registrationServices.numWeeksCourseOfInstructorBySupport(numInstructor, support);
        return ResponseEntity.ok(weeks);
    }
}
