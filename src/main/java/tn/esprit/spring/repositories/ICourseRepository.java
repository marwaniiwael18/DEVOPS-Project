package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Course;

public interface ICourseRepository extends JpaRepository<Course, Long> {

}
