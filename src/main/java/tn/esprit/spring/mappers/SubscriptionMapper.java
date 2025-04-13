package tn.esprit.spring.mappers;

import org.springframework.stereotype.Component;
import tn.esprit.spring.dto.SubscriptionDTO;
import tn.esprit.spring.entities.Subscription;

@Component
public class SubscriptionMapper {

    public SubscriptionDTO toDTO(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setNumSub(subscription.getNumSub());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setPrice(subscription.getPrice());
        dto.setTypeSub(subscription.getTypeSub());
        
        return dto;
    }
    
    public Subscription toEntity(SubscriptionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Subscription subscription = new Subscription();
        subscription.setNumSub(dto.getNumSub());
        subscription.setStartDate(dto.getStartDate());
        subscription.setEndDate(dto.getEndDate());
        subscription.setPrice(dto.getPrice());
        subscription.setTypeSub(dto.getTypeSub());
        
        return subscription;
    }
}
