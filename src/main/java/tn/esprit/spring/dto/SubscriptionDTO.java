package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.spring.entities.TypeSubscription;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @Positive(message = "Price must be positive")
    private Float price;
    
    @NotNull(message = "Subscription type is required")
    private TypeSubscription typeSub;

    private Long numSub;
    private LocalDate endDate;

    public Long getNumSub() {
        return numSub;
    }

    public void setNumSub(Long numSub) {
        this.numSub = numSub;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
