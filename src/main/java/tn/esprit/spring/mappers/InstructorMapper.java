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
        dto.setId(instructor.getNumInstructor());
        dto.setFirstName(instructor.getFirstName());
        dto.setLastName(instructor.getLastName());
        dto.setDateOfHire(instructor.getDateOfHire());
        dto.setEmail(instructor.getEmail());
        dto.setPhone(instructor.getPhone());
        
        return dto;
    }
    
    public Instructor toEntity(InstructorDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Instructor instructor = new Instructor();
        instructor.setNumInstructor(dto.getId());
        instructor.setFirstName(dto.getFirstName());
        instructor.setLastName(dto.getLastName());
        instructor.setDateOfHire(dto.getDateOfHire());
        instructor.setEmail(dto.getEmail());
        instructor.setPhone(dto.getPhone());
        
        return instructor;
    }
}
