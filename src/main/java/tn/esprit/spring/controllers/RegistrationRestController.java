package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.IRegistrationServices;

import java.util.List;

@Tag(name = "\uD83D\uDDD3️Registration Management")
@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationRestController {
    private final IRegistrationServices registrationServices;

    @Operation(description = "Add Registration and Assign to Skier")
    @PutMapping("/addAndAssignToSkier/{numSkieur}")
    public Registration addAndAssignToSkier(@RequestBody Registration registration,
                                                     @PathVariable("numSkieur") Long numSkieur)
    {
        return  registrationServices.addRegistrationAndAssignToSkier(registration,numSkieur);
    }
    @Operation(description = "Assign Registration to Course")
    @PutMapping("/assignToCourse/{numRegis}/{numSkieur}")
    public Registration assignToCourse( @PathVariable("numRegis") Long numRegistration,
                                        @PathVariable("numSkieur") Long numCourse){
        return registrationServices.assignRegistrationToCourse(numRegistration, numCourse);
    }


    @Operation(description = "Add Registration and Assign to Skier and Course")
    @PutMapping("/addAndAssignToSkierAndCourse/{numSkieur}/{numCourse}")
    public Registration addAndAssignToSkierAndCourse(@RequestBody Registration registration,
                                                     @PathVariable("numSkieur") Long numSkieur,
                                                     @PathVariable("numCourse") Long numCourse)
    {
        return  registrationServices.addRegistrationAndAssignToSkierAndCourse(registration,numSkieur,numCourse);
    }

    @Operation(description = "Numbers of the weeks when an instructor has given lessons in a given support")
    @GetMapping("/numWeeks/{numInstructor}/{support}")
    public List<Integer> numWeeksCourseOfInstructorBySupport(@PathVariable("numInstructor")Long numInstructor,
                                                                  @PathVariable("support") Support support) {
        return registrationServices.numWeeksCourseOfInstructorBySupport(numInstructor,support);
    }
}
