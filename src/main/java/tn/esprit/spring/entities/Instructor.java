package tn.esprit.spring.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class Instructor implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long numInstructor;

	@NotNull
	@Size(min = 1, max = 50)
	String firstName;

	@NotNull
	@Size(min = 1, max = 50)
	String lastName;

	LocalDate dateOfHire;

	@Email
	String email;

	String phone;

	@OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
	Set<Course> courses = new HashSet<>();
	
	// Constructor without email and phone - for backward compatibility with tests
	public Instructor(Long numInstructor, String firstName, String lastName, LocalDate dateOfHire, Set<Course> courses) {
		this.numInstructor = numInstructor;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfHire = dateOfHire;
		this.courses = courses != null ? courses : new HashSet<>();
	}
	
	// Full constructor
	public Instructor(Long numInstructor, String firstName, String lastName, LocalDate dateOfHire, String email, 
			String phone, Set<Course> courses) {
		this.numInstructor = numInstructor;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfHire = dateOfHire;
		this.email = email;
		this.phone = phone;
		this.courses = courses != null ? courses : new HashSet<>();
	}
}
