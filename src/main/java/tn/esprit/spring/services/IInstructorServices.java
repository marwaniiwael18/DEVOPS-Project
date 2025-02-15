package tn.esprit.spring.services;

import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.entities.Support;

import java.util.List;

public interface IInstructorServices {

    Instructor addInstructor(Instructor instructor);

    List<Instructor> retrieveAllInstructors();

    Instructor updateInstructor(Instructor instructor);

    Instructor retrieveInstructor(Long numInstructor);

    Instructor addInstructorAndAssignToCourse(Instructor instructor, Long numCourse);

}
