package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private Long id;
    
    @Positive(message = "Week number must be positive")
    private Integer numWeek;
    
    // Optional: if you want to expose skier and course ids
    private Long skierId;
    private Long courseId;
}
