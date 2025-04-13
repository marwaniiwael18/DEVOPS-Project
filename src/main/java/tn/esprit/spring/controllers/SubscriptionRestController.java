package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dto.SubscriptionDTO;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.mappers.SubscriptionMapper;
import tn.esprit.spring.services.ISubscriptionServices;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "\uD83D\uDC65 Subscription Management")
@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionRestController {

    private final ISubscriptionServices subscriptionServices;
    private final SubscriptionMapper subscriptionMapper;

    @Operation(description = "Add Subscription ")
    @PostMapping("/add")
    public ResponseEntity<SubscriptionDTO> addSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO){
        Subscription subscription = subscriptionMapper.toEntity(subscriptionDTO);
        Subscription result = subscriptionServices.addSubscription(subscription);
        if (result == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(subscriptionMapper.toDTO(result));
    }
    
    @Operation(description = "Retrieve Subscription by Id")
    @GetMapping("/get/{id-subscription}")
    public ResponseEntity<SubscriptionDTO> getById(@PathVariable("id-subscription") Long numSubscription){
        Subscription subscription = subscriptionServices.retrieveSubscriptionById(numSubscription);
        if (subscription == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subscriptionMapper.toDTO(subscription));
    }
    
    @Operation(description = "Retrieve Subscriptions by Type")
    @GetMapping("/all/{typeSub}")
    public ResponseEntity<Set<SubscriptionDTO>> getSubscriptionsByType(@PathVariable("typeSub") TypeSubscription typeSubscription){
        Set<Subscription> subscriptions = subscriptionServices.getSubscriptionByType(typeSubscription);
        Set<SubscriptionDTO> subscriptionDTOs = subscriptions.stream()
                .map(subscriptionMapper::toDTO)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(subscriptionDTOs);
    }
    
    @Operation(description = "Update Subscription ")
    @PutMapping("/update")
    public ResponseEntity<SubscriptionDTO> updateSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO){
        Subscription subscription = subscriptionMapper.toEntity(subscriptionDTO);
        Subscription result = subscriptionServices.updateSubscription(subscription);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subscriptionMapper.toDTO(result));
    }
    
    @Operation(description = "Retrieve Subscriptions created between two dates")
    @GetMapping("/all/{date1}/{date2}")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsByDates(
            @PathVariable("date1") LocalDate startDate,
            @PathVariable("date2") LocalDate endDate){
        List<Subscription> subscriptions = subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate);
        List<SubscriptionDTO> subscriptionDTOs = subscriptions.stream()
                .map(subscriptionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscriptionDTOs);
    }

    @Operation(description = "Retrieve All Subscriptions")
    @GetMapping("/all")
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionServices.retrieveAllSubscriptions();
        List<SubscriptionDTO> subscriptionDTOs = subscriptions.stream()
                .map(subscriptionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscriptionDTOs);
    }
}
