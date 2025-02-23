package tn.esprit.spring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE)
@Entity
public class Course implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long numCourse;

	@Min(1)
	@Max(10)
	int level;

	@NotNull
	@Enumerated(EnumType.STRING)
	TypeCourse typeCourse;

	@NotNull
	@Enumerated(EnumType.STRING)
	Support support;

	@Positive
	Float price;

	@Min(1)
	@Max(10)
	int timeSlot;

	@JsonIgnore
	@OneToMany(mappedBy= "course", cascade = CascadeType.ALL, orphanRemoval = true)
	Set<Registration> registrations;

	@ManyToOne
	@JoinColumn(name = "instructor_id", nullable = true)
	private Instructor instructor;

	// Constructeur pour les tests
// ✅ **Ajout du constructeur utilisé dans les tests**
	public Course(Long numCourse, int level, TypeCourse typeCourse, Support support, Float price, int timeSlot) {
		this.numCourse = numCourse;
		this.level = level;
		this.typeCourse = typeCourse;
		this.support = support;
		this.price = price;
		this.timeSlot = timeSlot;
	}

	// ✅ **Autre constructeur prenant en charge Instructor**
	public Course(Long numCourse, int level, TypeCourse typeCourse, Support support, Float price, int timeSlot, Instructor instructor) {
		this.numCourse = numCourse;
		this.level = level;
		this.typeCourse = typeCourse;
		this.support = support;
		this.price = price;
		this.timeSlot = timeSlot;
		this.instructor = instructor;
	}
}
