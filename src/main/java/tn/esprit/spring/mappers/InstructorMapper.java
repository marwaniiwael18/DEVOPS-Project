package tn.esprit.spring.mappers;

import org.springframework.stereotype.Component;
import tn.esprit.spring.dto.InstructorDTO;
import tn.esprit.spring.entities.Instructor;

@Component
public class InstructorMapper {

    public InstructorDTO toDTO(Instructor instructor) {
        if (instructor == null) {
            return null;
        }

        InstructorDTO dto = new InstructorDTO();
        dto.setFirstName(instructor.getFirstName());
        dto.setLastName(instructor.getLastName());
        dto.setDateOfHire(instructor.getDateOfHire());
        dto.setCity(null); // City is not in entity but required in DTO
        dto.setEmail(instructor.getEmail());
        
        return dto;
    }

    public Instructor toEntity(InstructorDTO dto) {
        if (dto == null) {
            return null;
        }

        Instructor instructor = new Instructor();
        // No need to set ID, it will be generated or set elsewhere
        instructor.setFirstName(dto.getFirstName());
        instructor.setLastName(dto.getLastName());
        instructor.setDateOfHire(dto.getDateOfHire());
        instructor.setEmail(dto.getEmail());
        // No need to set phone, it's not in the DTO anymore
        
        return instructor;
    }
}
