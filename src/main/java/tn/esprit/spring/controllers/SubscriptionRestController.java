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
    public ResponseEntity<Subscription> createSubscription(
            @Valid @RequestBody Subscription subscription) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(subscriptionServices.addSubscription(subscription));
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
        return ResponseEntity.of(
                java.util.Optional.ofNullable(
                        subscriptionServices.retrieveSubscriptionById(numSubscription)
                )
        );
    }

    @Operation(summary = "Retrieve subscriptions by type")
    @GetMapping("/by-type/{type}")
    public ResponseEntity<Set<Subscription>> getSubscriptionsByType(
            @Parameter(description = "Subscription type", required = true)
            @PathVariable("type") TypeSubscription typeSubscription) {
        return ResponseEntity.ok(
                subscriptionServices.getSubscriptionByType(typeSubscription)
        );
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
        if (!id.equals(subscription.getNumSub())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(subscriptionServices.updateSubscription(subscription));
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

        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate)
        );
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
        // Add delete method to service and implement here
        return ResponseEntity.noContent().build();
    }
}