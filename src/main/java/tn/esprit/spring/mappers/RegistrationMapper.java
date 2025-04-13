package tn.esprit.spring.mappers;

import org.springframework.stereotype.Component;
import tn.esprit.spring.dto.RegistrationDTO;
import tn.esprit.spring.entities.Registration;

@Component
public class RegistrationMapper {

    public RegistrationDTO toDTO(Registration registration) {
        if (registration == null) {
            return null;
        }
        
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(registration.getNumRegistration());
        dto.setNumWeek(registration.getNumWeek());
        
        // Set related IDs if available
        if (registration.getSkier() != null) {
            dto.setSkierId(registration.getSkier().getNumSkier());
        }
        
        if (registration.getCourse() != null) {
            dto.setCourseId(registration.getCourse().getNumCourse());
        }
        
        return dto;
    }
    
    public Registration toEntity(RegistrationDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Registration registration = new Registration();
        registration.setNumRegistration(dto.getId());
        registration.setNumWeek(dto.getNumWeek());
        
        // Note: skier and course relationships are typically set in the service layer
        // not directly from DTO to preserve proper encapsulation
        
        return registration;
    }
}
