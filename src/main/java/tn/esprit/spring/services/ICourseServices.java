package tn.esprit.spring.services;

import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse;

import java.util.List;

public interface ICourseServices {

    List<Course> retrieveAllCourses();

    Course  addCourse(Course  course);

    Course updateCourse(Course course);

    Course retrieveCourse(Long numCourse);


}
