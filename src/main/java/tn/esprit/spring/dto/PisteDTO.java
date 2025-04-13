package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.spring.entities.Color;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PisteDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    private String name;
    
    @NotNull(message = "Color is required")
    private Color color;
    
    @Positive(message = "Length must be positive")
    private Integer length;
    
    @Positive(message = "Slope must be positive")
    private Integer slope;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
