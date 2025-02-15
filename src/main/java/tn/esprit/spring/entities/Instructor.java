package tn.esprit.spring.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
public class Instructor implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long numInstructor;
	String firstName;
	String lastName;
	LocalDate dateOfHire;
	@OneToMany
	Set<Course> courses;
}
