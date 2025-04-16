package tn.esprit.spring.entities;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Subscription implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long numSub;

	@NotNull(message = "Start date is required")
	@FutureOrPresent(message = "Start date must be today or in the future")
	private LocalDate startDate;

	private LocalDate endDate;

	@NotNull(message = "Price is required")
	@Positive(message = "Price must be positive")
	private Float price;

	@NotNull(message = "Subscription type is required")
	@Enumerated(EnumType.STRING)
	private TypeSubscription typeSub;
}