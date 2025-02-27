package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.repositories.IPisteRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@AllArgsConstructor
@Service
public class PisteServicesImpl implements  IPisteServices {

    private final IPisteRepository pisteRepository;

    @Override
    public List<Piste> retrieveAllPistes() {
        return pisteRepository.findAll();
    }

    @Override
    public Piste addPiste(Piste piste) {
        return pisteRepository.save(piste);
    }

    @Override
    public Piste retrievePiste(Long numPiste) {
        return pisteRepository.findById(numPiste).orElse(null);
    }

    @Override
    public void removePiste(Long id) {
        Piste piste = pisteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Piste not found with id " + id));
        pisteRepository.delete(piste); // Assurez-vous d'appeler delete sur l'entité, pas deleteById
    }
}
