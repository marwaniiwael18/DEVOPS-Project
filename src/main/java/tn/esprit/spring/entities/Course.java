package tn.esprit.spring.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long numCourse;

	private Integer level;

	@Enumerated(EnumType.STRING)
	private TypeCourse typeCourse;

	@Enumerated(EnumType.STRING)
	private Support support;

	private Float price;

	@ManyToOne
	private Instructor instructor;

	// Add getters, setters, and other required annotations
}