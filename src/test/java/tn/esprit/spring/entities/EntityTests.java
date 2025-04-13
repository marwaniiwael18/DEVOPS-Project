package tn.esprit.spring.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EntityTests {

    @Test
    void testCourseEntity() {
        // Test no-args constructor
        Course course1 = new Course();
        assertNull(course1.getNumCourse());
        
        // Test fields assignment
        course1.setNumCourse(1L);
        course1.setLevel(3);
        course1.setTypeCourse(TypeCourse.INDIVIDUAL);
        course1.setSupport(Support.SKI);
        course1.setPrice(120.0f);
        course1.setTimeSlot(5);
        
        assertEquals(1L, course1.getNumCourse());
        assertEquals(3, course1.getLevel());
        assertEquals(TypeCourse.INDIVIDUAL, course1.getTypeCourse());
        assertEquals(Support.SKI, course1.getSupport());
        assertEquals(120.0f, course1.getPrice());
        assertEquals(5, course1.getTimeSlot());
        
        // Test parameterized constructor
        Course course2 = new Course(2L, 4, TypeCourse.COLLECTIVE_CHILDREN, Support.SNOWBOARD, 150.0f, 6);
        assertEquals(2L, course2.getNumCourse());
        assertEquals(4, course2.getLevel());
        assertEquals(TypeCourse.COLLECTIVE_CHILDREN, course2.getTypeCourse());
        assertEquals(Support.SNOWBOARD, course2.getSupport());
        assertEquals(150.0f, course2.getPrice());
        assertEquals(6, course2.getTimeSlot());
        
        // Test instructor relationship
        Instructor instructor = new Instructor();
        instructor.setNumInstructor(1L);
        
        Course course3 = new Course(3L, 5, TypeCourse.COLLECTIVE_ADULT, Support.SKI, 180.0f, 7, instructor);
        assertEquals(instructor, course3.getInstructor());
        
        // Test registrations relationship
        Set<Registration> registrations = new HashSet<>();
        Registration registration = new Registration();
        registration.setNumRegistration(1L);
        registrations.add(registration);
        
        course1.setRegistrations(registrations);
        assertEquals(1, course1.getRegistrations().size());
        assertTrue(course1.getRegistrations().contains(registration));
        
        // Test constructor with null instructor
        Course course4 = new Course(4L, 2, TypeCourse.COLLECTIVE_ADULT, Support.SNOWBOARD, 100.0f, 3, null);
        assertEquals(4L, course4.getNumCourse());
        assertEquals(2, course4.getLevel());
        assertEquals(TypeCourse.COLLECTIVE_ADULT, course4.getTypeCourse());
        assertEquals(Support.SNOWBOARD, course4.getSupport());
        assertEquals(100.0f, course4.getPrice());
        assertEquals(3, course4.getTimeSlot());
        assertNull(course4.getInstructor());
        
        // Test default values
        Course emptyCourse = new Course();
        assertNull(emptyCourse.getRegistrations());
        emptyCourse.setRegistrations(new HashSet<>());
        assertTrue(emptyCourse.getRegistrations().isEmpty());
    }
    
    @Test
    void testCourseAllArgsConstructor() {
        // Test the @AllArgsConstructor generated constructor
        Long numCourse = 5L;
        int level = 7;
        TypeCourse typeCourse = TypeCourse.INDIVIDUAL;
        Support support = Support.SKI;
        float price = 200.0f;
        int timeSlot = 4;
        Set<Registration> registrations = new HashSet<>();
        Registration registration = new Registration();
        registration.setNumRegistration(2L);
        registrations.add(registration);
        Instructor instructor = new Instructor();
        instructor.setNumInstructor(2L);
        
        // Create course with all-args constructor
        Course course = new Course(numCourse, level, typeCourse, support, price, timeSlot, registrations, instructor);
        
        // Verify all fields are set correctly
        assertEquals(numCourse, course.getNumCourse());
        assertEquals(level, course.getLevel());
        assertEquals(typeCourse, course.getTypeCourse());
        assertEquals(support, course.getSupport());
        assertEquals(price, course.getPrice());
        assertEquals(timeSlot, course.getTimeSlot());
        assertEquals(registrations, course.getRegistrations());
        assertEquals(instructor, course.getInstructor());
        assertEquals(1, course.getRegistrations().size());
        assertTrue(course.getRegistrations().contains(registration));
    }

    @Test
    void testInstructorEntity() {
        // Test no-args constructor
        Instructor instructor1 = new Instructor();
        assertNull(instructor1.getNumInstructor());
        
        // Test fields assignment
        instructor1.setNumInstructor(1L);
        instructor1.setFirstName("John");
        instructor1.setLastName("Doe");
        LocalDate hireDate = LocalDate.of(2022, 1, 1);
        instructor1.setDateOfHire(hireDate);
        
        assertEquals(1L, instructor1.getNumInstructor());
        assertEquals("John", instructor1.getFirstName());
        assertEquals("Doe", instructor1.getLastName());
        assertEquals(hireDate, instructor1.getDateOfHire());
        
        // Test courses relationship
        Set<Course> courses = new HashSet<>();
        Course course = new Course();
        course.setNumCourse(1L);
        courses.add(course);
        
        instructor1.setCourses(courses);
        assertEquals(1, instructor1.getCourses().size());
        assertTrue(instructor1.getCourses().contains(course));
        
        // Test all-args constructor
        Instructor instructor2 = new Instructor(2L, "Jane", "Smith", LocalDate.of(2021, 5, 10), new HashSet<>());
        assertEquals(2L, instructor2.getNumInstructor());
        assertEquals("Jane", instructor2.getFirstName());
        assertEquals("Smith", instructor2.getLastName());
    }

    @Test
    void testSkierEntity() {
        // Test no-args constructor
        Skier skier1 = new Skier();
        assertNull(skier1.getNumSkier());
        
        // Test fields assignment
        skier1.setNumSkier(1L);
        skier1.setFirstName("John");
        skier1.setLastName("Doe");
        LocalDate dob = LocalDate.of(1990, 1, 1);
        skier1.setDateOfBirth(dob);
        skier1.setCity("New York");
        
        assertEquals(1L, skier1.getNumSkier());
        assertEquals("John", skier1.getFirstName());
        assertEquals("Doe", skier1.getLastName());
        assertEquals(dob, skier1.getDateOfBirth());
        assertEquals("New York", skier1.getCity());
        
        // Test relationships
        Subscription subscription = new Subscription();
        subscription.setNumSub(1L);
        skier1.setSubscription(subscription);
        assertEquals(subscription, skier1.getSubscription());
        
        Set<Piste> pistes = new HashSet<>();
        Piste piste = new Piste();
        piste.setNumPiste(1L);
        pistes.add(piste);
        skier1.setPistes(pistes);
        assertEquals(1, skier1.getPistes().size());
        
        // Test constructors
        Skier skier2 = new Skier(2L, "Jane");
        assertEquals(2L, skier2.getNumSkier());
        assertEquals("Jane", skier2.getFirstName());
        
        Skier skier3 = new Skier(3L, "Bob", "Smith");
        assertEquals(3L, skier3.getNumSkier());
        assertEquals("Bob", skier3.getFirstName());
        assertEquals("Smith", skier3.getLastName());
        
        Skier skier4 = new Skier(4L, "Alice", "Johnson", subscription);
        assertEquals(4L, skier4.getNumSkier());
        assertEquals(subscription, skier4.getSubscription());
    }

    @Test
    void testSkierAllArgsConstructor() {
        // Create test data for all fields
        Long numSkier = 10L;
        String firstName = "Thomas";
        String lastName = "Anderson";
        LocalDate dateOfBirth = LocalDate.of(1995, 3, 15);
        String city = "Aspen";
        
        // Create subscription for relationship
        Subscription subscription = new Subscription();
        subscription.setNumSub(5L);
        
        // Create pistes for relationship
        Set<Piste> pistes = new HashSet<>();
        Piste piste = new Piste();
        piste.setNumPiste(3L);
        pistes.add(piste);
        
        // Create registrations for relationship
        Set<Registration> registrations = new HashSet<>();
        Registration registration = new Registration();
        registration.setNumRegistration(7L);
        registrations.add(registration);
        
        // Use the all-args constructor
        Skier skier = new Skier(numSkier, firstName, lastName, dateOfBirth, city, subscription, pistes, registrations);
        
        // Verify all fields were properly set
        assertEquals(numSkier, skier.getNumSkier(), "Num skier should match");
        assertEquals(firstName, skier.getFirstName(), "First name should match");
        assertEquals(lastName, skier.getLastName(), "Last name should match");
        assertEquals(dateOfBirth, skier.getDateOfBirth(), "Date of birth should match");
        assertEquals(city, skier.getCity(), "City should match");
        assertEquals(subscription, skier.getSubscription(), "Subscription should match");
        assertEquals(pistes, skier.getPistes(), "Pistes should match");
        assertEquals(registrations, skier.getRegistrations(), "Registrations should match");
        assertEquals(1, skier.getPistes().size(), "Should have one piste");
        assertEquals(1, skier.getRegistrations().size(), "Should have one registration");
    }

    @Test
    void testSubscriptionEntity() {
        // Test no-args constructor
        Subscription subscription1 = new Subscription();
        assertNull(subscription1.getNumSub());
        
        // Test fields assignment
        subscription1.setNumSub(1L);
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        subscription1.setStartDate(startDate);
        subscription1.setEndDate(endDate);
        subscription1.setPrice(500.0f);
        subscription1.setTypeSub(TypeSubscription.ANNUAL);
        
        assertEquals(1L, subscription1.getNumSub());
        assertEquals(startDate, subscription1.getStartDate());
        assertEquals(endDate, subscription1.getEndDate());
        assertEquals(500.0f, subscription1.getPrice());
        assertEquals(TypeSubscription.ANNUAL, subscription1.getTypeSub());
        
        // Test second constructor
        Subscription subscription2 = new Subscription(2L, TypeSubscription.MONTHLY, startDate, 200.0f);
        assertEquals(2L, subscription2.getNumSub());
        assertEquals(TypeSubscription.MONTHLY, subscription2.getTypeSub());
        assertEquals(startDate, subscription2.getStartDate());
        assertEquals(200.0f, subscription2.getPrice());
        
        // Test all-args constructor
        Subscription subscription3 = new Subscription(3L, startDate, endDate, 300.0f, TypeSubscription.SEMESTRIEL);
        assertEquals(3L, subscription3.getNumSub());
        assertEquals(startDate, subscription3.getStartDate());
        assertEquals(endDate, subscription3.getEndDate());
        assertEquals(300.0f, subscription3.getPrice());
        assertEquals(TypeSubscription.SEMESTRIEL, subscription3.getTypeSub());
    }

    @Test
    void testSubscriptionToString() {
        // Create a subscription with all fields populated
        Long numSub = 10L;
        LocalDate startDate = LocalDate.of(2023, 5, 15);
        LocalDate endDate = LocalDate.of(2023, 11, 15);
        float price = 350.0f;
        TypeSubscription typeSub = TypeSubscription.SEMESTRIEL;

        Subscription subscription = new Subscription(numSub, startDate, endDate, price, typeSub);

        // Get the toString result
        String toStringResult = subscription.toString();
        
        // Verify toString contains the key information
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains(numSub.toString()), "toString should contain the subscription number");
        assertTrue(toStringResult.contains(typeSub.toString()), "toString should contain the subscription type");
        assertTrue(toStringResult.contains(String.valueOf(price)), "toString should contain the price");
        assertTrue(toStringResult.contains(startDate.toString()), "toString should contain the start date");
        assertTrue(toStringResult.contains(endDate.toString()), "toString should contain the end date");
    }

    @Test
    void testRegistrationEntity() {
        // Test no-args constructor
        Registration registration1 = new Registration();
        assertNull(registration1.getNumRegistration());
        
        // Test fields assignment
        registration1.setNumRegistration(1L);
        registration1.setNumWeek(10);
        
        assertEquals(1L, registration1.getNumRegistration());
        assertEquals(10, registration1.getNumWeek());
        
        // Test relationships
        Skier skier = new Skier();
        skier.setNumSkier(1L);
        registration1.setSkier(skier);
        assertEquals(skier, registration1.getSkier());
        
        Course course = new Course();
        course.setNumCourse(1L);
        registration1.setCourse(course);
        assertEquals(course, registration1.getCourse());
        
        // Test all-args constructor
        Registration registration2 = new Registration(2L, 20, skier, course);
        assertEquals(2L, registration2.getNumRegistration());
        assertEquals(20, registration2.getNumWeek());
        assertEquals(skier, registration2.getSkier());
        assertEquals(course, registration2.getCourse());
        
        // Test toString method
        String toString = registration1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
    }

    @Test
    void testPisteEntity() {
        // Test no-args constructor
        Piste piste = new Piste();
        assertNull(piste.getNumPiste());
        
        // Test fields assignment using setters
        piste.setNumPiste(5L);
        piste.setColor(Color.BLACK);
        
        // Since we don't know the exact fields in Piste, test what we know exists
        assertEquals(5L, piste.getNumPiste());
        assertEquals(Color.BLACK, piste.getColor());
        
        // Test relationships
        Set<Skier> skiers = new HashSet<>();
        Skier skier = new Skier();
        skier.setNumSkier(7L);
        skiers.add(skier);
        
        piste.setSkiers(skiers);
        assertEquals(1, piste.getSkiers().size());
        assertTrue(piste.getSkiers().contains(skier));
    }

    @Test
    void testPisteAllArgsConstructor() {
        // Test data for the Piste constructor
        Long numPiste = 8L;
        String namePiste = "Black Diamond";
        Color color = Color.RED;
        int length = 2000;
        int slope = 45;
        
        // Create skiers for relationship
        Set<Skier> skiers = new HashSet<>();
        Skier skier = new Skier();
        skier.setNumSkier(10L);
        skiers.add(skier);
        
        // Create a Piste with the all-args constructor
        // Parameters in order: numPiste, namePiste, color, length, slope, skiers
        Piste piste = new Piste(numPiste, namePiste, color, length, slope, skiers);
        
        // Verify fields
        assertEquals(numPiste, piste.getNumPiste(), "Num piste should match");
        assertEquals(namePiste, piste.getNamePiste(), "Name should match");
        assertEquals(color, piste.getColor(), "Color should match");
        assertEquals(length, piste.getLength(), "Length should match");
        assertEquals(slope, piste.getSlope(), "Slope should match");
        assertEquals(skiers, piste.getSkiers(), "Skiers set should match");
        assertEquals(1, piste.getSkiers().size(), "Should have one skier");
    }
    
    @ParameterizedTest
    @EnumSource(Color.class)
    void testColorEnum(Color color) {
        assertNotNull(color);
        switch (color) {
            case GREEN:
            case BLUE:
            case RED:
            case BLACK:
                // Valid enum values
                break;
            default:
                fail("Unknown Color: " + color);
        }
    }

    @ParameterizedTest
    @EnumSource(TypeCourse.class)
    void testTypeCourseEnum(TypeCourse typeCourse) {
        assertNotNull(typeCourse);
        
        switch (typeCourse) {
            case COLLECTIVE_CHILDREN:
            case COLLECTIVE_ADULT:
            case INDIVIDUAL:
                // Valid enum values
                break;
            default:
                fail("Unknown TypeCourse: " + typeCourse);
        }
    }

    @ParameterizedTest
    @EnumSource(Support.class)
    void testSupportEnum(Support support) {
        assertNotNull(support);
        assertTrue(support == Support.SKI || support == Support.SNOWBOARD);
    }

    @ParameterizedTest
    @EnumSource(TypeSubscription.class)
    void testTypeSubscriptionEnum(TypeSubscription typeSubscription) {
        assertNotNull(typeSubscription);
        assertTrue(typeSubscription == TypeSubscription.ANNUAL || 
                   typeSubscription == TypeSubscription.MONTHLY || 
                   typeSubscription == TypeSubscription.SEMESTRIEL);
    }
}
