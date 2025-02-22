package tn.esprit.spring.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE)
@Entity
public class Course implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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
	@OneToMany(mappedBy= "course")
	Set<Registration> registrations;


}
