package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import tn.esprit.spring.entities.*;

import java.util.List;

public interface IRegistrationRepository extends CrudRepository<Registration, Long> {

    long countByCourseAndNumWeek(Course course, int numWeek);
    @Query("SELECT reg.numWeek FROM Registration reg " +
            "JOIN reg.course course " + // Join Registration with Course
            "JOIN course.instructor instructor " + // Join Course with Instructor
            "WHERE instructor.numInstructor = :idIns AND course.support = :support")
    List<Integer> numWeeksCourseOfInstructorBySupport(
            @Param("idIns") Long numInstructor,
            @Param("support") Support support);
    @Query("select count(distinct r) from Registration r " +
            "where r.numWeek = ?1 and r.skier.numSkier = ?2 and r.course.numCourse = ?3")
    long countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(int numWeek, Long numSkier, Long numCourse);

  //  long countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(int numWeek, Long numSkier, Long numCourse);






}
