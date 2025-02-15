package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;

import java.util.List;

public interface ISkierRepository extends JpaRepository<Skier, Long> {
   List<Skier> findBySubscription_TypeSub(TypeSubscription typeSubscription);
   Skier findBySubscription(Subscription subscription);


}
