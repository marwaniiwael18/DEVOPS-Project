package tn.esprit.spring.entities;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Subscription implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long numSub;

	private LocalDate startDate;
	private LocalDate endDate;
	private Float price;

	@Enumerated(EnumType.STRING)
	private TypeSubscription typeSub;
}