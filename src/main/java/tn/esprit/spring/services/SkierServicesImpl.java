package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class SkierServicesImpl implements ISkierServices {

    private ISkierRepository skierRepository;

    private IPisteRepository pisteRepository;

    private ICourseRepository courseRepository;

    private IRegistrationRepository registrationRepository;

    private ISubscriptionRepository subscriptionRepository;


    @Override
    public List<Skier> retrieveAllSkiers() {
        return skierRepository.findAll();
    }

    @Override
    public Skier addSkier(Skier skier) {
        switch (skier.getSubscription().getTypeSub()) {
            case ANNUAL:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusYears(1));
                break;
            case SEMESTRIEL:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(6));
                break;
            case MONTHLY:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(1));
                break;
        }
        return skierRepository.save(skier);
    }

    @Override
    public Skier assignSkierToSubscription(Long numSkier, Long numSubscription) {
        Skier skier = skierRepository.findById(numSkier).orElse(null);
        Subscription subscription = subscriptionRepository.findById(numSubscription).orElse(null);
        skier.setSubscription(subscription);
        return skierRepository.save(skier);
    }

    @Override
    public Skier addSkierAndAssignToCourse(Skier skier, Long numCourse) {
        Skier savedSkier = skierRepository.save(skier);
        Course course = courseRepository.getById(numCourse);
        Set<Registration> registrations = savedSkier.getRegistrations();
        for (Registration r : registrations) {
            r.setSkier(savedSkier);
            r.setCourse(course);
            registrationRepository.save(r);
        }
        return savedSkier;
    }

    @Override
    public void removeSkier(Long numSkier) {
        skierRepository.deleteById(numSkier);
    }

    @Override
    public Skier retrieveSkier(Long numSkier) {
        return skierRepository.findById(numSkier).orElse(null);
    }

    @Override
    public Skier assignSkierToPiste(Long numSkieur, Long numPiste) {
        Skier skier = skierRepository.findById(numSkieur).orElse(null);
        Piste piste = pisteRepository.findById(numPiste).orElse(null);
        try {
            skier.getPistes().add(piste);
        } catch (NullPointerException exception) {
            Set<Piste> pisteList = new HashSet<>();
            pisteList.add(piste);
            skier.setPistes(pisteList);
        }

        return skierRepository.save(skier);
    }

    @Override
    public List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription) {
        return skierRepository.findBySubscription_TypeSub(typeSubscription);
    }
}
