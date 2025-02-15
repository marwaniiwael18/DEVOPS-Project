package tn.esprit.spring.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Instructor;



public interface IInstructorRepository extends JpaRepository<Instructor, Long> {

}
