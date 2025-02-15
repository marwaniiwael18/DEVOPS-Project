package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.spring.entities.Piste;

public interface IPisteRepository extends JpaRepository<Piste, Long> {

}
