package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    
    @NotNull(message = "Registration number is required")
    private Long numRegistration;
    
    @Positive(message = "Week number must be positive")
    private Integer numWeek;

    private Long id;
    private Long skierId;
    private Long courseId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkierId() {
        return skierId;
    }

    public void setSkierId(Long skierId) {
        this.skierId = skierId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
