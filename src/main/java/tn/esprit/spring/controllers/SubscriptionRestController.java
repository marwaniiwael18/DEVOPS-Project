package tn.esprit.spring.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.ISubscriptionServices;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Tag(name = "Subscription Management", description = "APIs for managing subscriptions")
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Validated
public class SubscriptionRestController {

    private final ISubscriptionServices subscriptionServices;

    @Operation(summary = "Create a new subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Subscription> createSubscription(@Valid @RequestBody Subscription subscription) {
        validateSubscription(subscription);
        try {
            Subscription created = subscriptionServices.addSubscription(subscription);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid subscription data");
        }
    }

    @Operation(summary = "Retrieve a subscription by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the subscription"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscription(
            @Parameter(description = "Subscription ID", required = true)
            @PathVariable("id") Long numSubscription) {
        Subscription subscription = subscriptionServices.retrieveSubscriptionById(numSubscription);
        if (subscription == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subscription);
    }

    @Operation(summary = "Retrieve subscriptions by type")
    @GetMapping("/by-type/{type}")
    public ResponseEntity<Set<Subscription>> getSubscriptionsByType(
            @Parameter(description = "Subscription type", required = true)
            @PathVariable("type") TypeSubscription typeSubscription) {
        Set<Subscription> subscriptions = subscriptionServices.getSubscriptionByType(typeSubscription);
        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "Update an existing subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(
            @PathVariable("id") Long id,
            @Valid @RequestBody Subscription subscription) {

        // Validate ID match
        if (!id.equals(subscription.getNumSub())) {
            return ResponseEntity.badRequest().build();
        }

        // Validate subscription fields
        validateSubscription(subscription);

        try {
            // Set the ID and update
            subscription.setNumSub(id);
            Subscription updated = subscriptionServices.updateSubscription(subscription);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating subscription");
        }
    }

    @Operation(summary = "Retrieve subscriptions between dates")
    @GetMapping("/by-date-range")
    public ResponseEntity<List<Subscription>> getSubscriptionsByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @NotNull LocalDate startDate,

            @Parameter(description = "End date (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @NotNull LocalDate endDate) {

        validateDateRange(startDate, endDate);
        List<Subscription> subscriptions =
                subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate);
        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "Delete a subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscription deleted"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable("id") Long id) {
        Subscription subscription = subscriptionServices.retrieveSubscriptionById(id);
        if (subscription == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private void validateSubscription(Subscription subscription) {
        if (subscription == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subscription cannot be null");
        }

        if (subscription.getStartDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date is required");
        }

        if (subscription.getPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price is required");
        }

        if (subscription.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be positive");
        }

        if (subscription.getTypeSub() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subscription type is required");
        }

        if (subscription.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date cannot be in the past");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Both start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date must be before or equal to end date"
            );
        }
    }
}