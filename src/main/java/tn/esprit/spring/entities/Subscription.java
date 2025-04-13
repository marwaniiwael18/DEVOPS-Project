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

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Column(nullable = false)
	private Float price;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TypeSubscription typeSub;
}