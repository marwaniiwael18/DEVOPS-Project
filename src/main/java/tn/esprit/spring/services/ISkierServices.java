package tn.esprit.spring.services;

import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.TypeSubscription;

import java.util.List;

public interface ISkierServices {

	List<Skier> retrieveAllSkiers();

	Skier  addSkier(Skier  skier);

	Skier assignSkierToSubscription(Long numSkier, Long numSubscription);

	Skier addSkierAndAssignToCourse(Skier skier, Long numCourse);

	boolean removeSkier (Long numSkier);

	Skier retrieveSkier (Long numSkier);


	Skier assignSkierToPiste(Long numSkieur, Long numPiste);

	List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription);

}
