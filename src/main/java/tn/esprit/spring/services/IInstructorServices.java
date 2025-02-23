package tn.esprit.spring.services;


import tn.esprit.spring.entities.Instructor;


import java.util.List;

public interface IInstructorServices {

    Instructor addInstructor(Instructor instructor);

    List<Instructor> retrieveAllInstructors();

    Instructor updateInstructor(Instructor instructor);

    Instructor retrieveInstructor(Long numInstructor);

    Instructor addInstructorAndAssignToCourse(Instructor instructor, Long numCourse);
    void deleteInstructor(Long numInstructor); // Ajout de la méthode de suppression


}
