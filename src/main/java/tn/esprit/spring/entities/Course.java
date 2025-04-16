package tn.esprit.spring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@ToString
public class Course implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long numCourse;

	@Column(nullable = false)
	Integer level;

	@Column(nullable = false)
	Float price;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	TypeCourse typeCourse;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	Support support;

	@ManyToOne
	Instructor instructor;

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
	@JsonIgnore
	Set<Registration> registrations = new HashSet<>();
}