package tn.esprit.spring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@ToString
public class Instructor implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long numInstructor;

	@Column(nullable = false)
	String firstName;

	@Column(nullable = false)
	String lastName;

	@Column(nullable = false)
	LocalDate dateOfHire;

	@OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Course> courses = new HashSet<>(); // Made private

	// Utility method to manage bidirectional relationship
	public void addCourse(Course course) {
		courses.add(course);
		course.setInstructor(this);
	}

	// Utility method to manage bidirectional relationship
	public void removeCourse(Course course) {
		courses.remove(course);
		course.setInstructor(null);
	}
}