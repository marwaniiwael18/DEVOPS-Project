package tn.esprit.spring.mappers;

import org.springframework.stereotype.Component;
import tn.esprit.spring.dto.PisteDTO;
import tn.esprit.spring.entities.Piste;

@Component
public class PisteMapper {

    public PisteDTO toDTO(Piste piste) {
        if (piste == null) {
            return null;
        }
        
        PisteDTO dto = new PisteDTO();
        dto.setId(piste.getNumPiste());
        dto.setName(piste.getNamePiste());
        dto.setColor(piste.getColor());
        dto.setLength(piste.getLength());
        dto.setSlope(piste.getSlope());
        
        return dto;
    }
    
    public Piste toEntity(PisteDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Piste piste = new Piste();
        piste.setNumPiste(dto.getId());
        piste.setNamePiste(dto.getName());
        piste.setColor(dto.getColor());
        piste.setLength(dto.getLength());
        piste.setSlope(dto.getSlope());
        
        return piste;
    }
}
