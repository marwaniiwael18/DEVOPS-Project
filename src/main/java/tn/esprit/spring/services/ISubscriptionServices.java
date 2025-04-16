package tn.esprit.spring.services;

import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ISubscriptionServices {
	Subscription addSubscription(Subscription subscription);
	Subscription updateSubscription(Subscription subscription);
	Subscription retrieveSubscriptionById(Long numSubscription);
	Set<Subscription> getSubscriptionByType(TypeSubscription type);
	List<Subscription> retrieveSubscriptionsByDates(LocalDate startDate, LocalDate endDate);
	void retrieveSubscriptions();
}