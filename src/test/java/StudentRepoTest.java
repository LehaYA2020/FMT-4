import dao.CourseRepository;
import dao.Exceptions.DAOException;
import dao.ScriptExecutor;
import dao.StudentRepository;
import models.Course;
import models.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StudentRepoTest {
    private static final List<Student> students = new ArrayList<>();
    private static final List<Course> courses = new ArrayList<>();
    private final StudentRepository studentRepository = new StudentRepository();
    private final CourseRepository courseRepository = new CourseRepository();

    @BeforeAll
    public static void prepare() throws DAOException {
        for (int i = 1; i <= 3; i++) {
            students.add(new Student(i, "Student-" + i, "forTest"));
            courses.add(new Course("Course-" + i, "forTest"));
        }
    }

    @BeforeEach
    public void createTables() throws DAOException {

        ScriptExecutor scriptExecutor = new ScriptExecutor();
        scriptExecutor.executeScript("CreateTables.sql");
    }

    @Test
    public void shouldInsertStudentsIntoDB() throws DAOException {
        List<Student> studentsInDB = studentRepository.getAllStudents();
        assertEquals(0, studentsInDB.size());

        studentRepository.insertStudent(students);

        studentsInDB = studentRepository.getAllStudents();
        assertEquals(students, studentsInDB);
    }

    @Test
    public void shouldGetAllStudentsFromDB() throws DAOException {
        studentRepository.insertStudent(students);
        List<Student> studentsInDB = studentRepository.getAllStudents();
        assertEquals(students, studentsInDB);
    }

    @Test
    public void shouldGetStudentById() throws DAOException {
        studentRepository.insertStudent(students);
        Student studentInDB = studentRepository.getStudentById(1);
        assertEquals(students.get(0), studentInDB);
    }

    @Test
    public void shouldAssignStudentToCourse() throws DAOException {
        studentRepository.insertStudent(students);
        courseRepository.insertCourses(courses);
        studentRepository.assignToCourse(students.get(0), courses.get(1));
        List<Course> expectedAssignments = new ArrayList<>();
        expectedAssignments.add(courses.get(1));
        assertEquals(studentRepository.getStudentAssignments(studentRepository.getStudentById(1)), expectedAssignments);
    }

    @Test
    public void shouldAssignStudentsToCourses() throws DAOException {
        studentRepository.insertStudent(students);
        courseRepository.insertCourses(courses);
        Map<Student, Set<Course>> assignMap = new HashMap<>();
        Set<Course> courseSet = new HashSet<>(courses);
        assignMap.put(students.get(0), courseSet);
        assignMap.put(students.get(1), new HashSet<>(Collections.singleton(courses.get(1))));

        studentRepository.assignToCourses(assignMap);

        for (Student student : students) {
            List<Course> actual = studentRepository.getStudentAssignments(student);
            List<Course> expected;
            if (assignMap.containsKey(student)) {
                expected = new ArrayList<>(assignMap.get(student));
            } else {
                expected = new ArrayList<>();
            }
            assertEquals(expected, actual);
        }
    }

    @Test
    public void shouldGetStudentsByCourseName() throws DAOException {
        studentRepository.insertStudent(students);
        courseRepository.insertCourses(courses);
        Map<Student, Set<Course>> assignMap = new HashMap<>();
        Set<Course> courseSet = new HashSet<>(courses);
        assignMap.put(students.get(0), courseSet);
        assignMap.put(students.get(1), new HashSet<>(Collections.singleton(courses.get(0))));
        studentRepository.assignToCourses(assignMap);

        List<Student> actualStudents = studentRepository.getStudentsByCourseName(courses.get(1).getName());
        List<Student> expectedStudents = students.subList(0, 1);

        assertEquals(expectedStudents, actualStudents);
    }

    @Test
    public void shouldDeleteFromCourse() throws DAOException {
        studentRepository.insertStudent(students);
        courseRepository.insertCourses(courses);
        Map<Student, Set<Course>> assignMap = new HashMap<>();
        Set<Course> courseSet = new HashSet<>(courses);
        assignMap.put(students.get(0), courseSet);
        assignMap.put(students.get(1), courseSet);
        studentRepository.assignToCourses(assignMap);
        List<Course> actualAssignments = studentRepository.getStudentAssignments(students.get(0));
        assertEquals(courses, actualAssignments);
        actualAssignments = studentRepository.getStudentAssignments(students.get(1));
        assertEquals(courses, actualAssignments);

        studentRepository.deleteStudentFromCourse(students.get(0), courses.get(0));

        actualAssignments = studentRepository.getStudentAssignments(students.get(1));
        assertEquals(courses, actualAssignments);
        actualAssignments = studentRepository.getStudentAssignments(students.get(0));
        assertEquals(courses.subList(1, 3), actualAssignments);
    }

    @Test
    public void shouldDeleteStudent() throws DAOException {
        List<Student> expected = students.subList(1, 3);
        studentRepository.insertStudent(students);
        studentRepository.deleteStudent(students.get(0));
        List<Student> actual = studentRepository.getAllStudents();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetStudentAssignments() throws DAOException {
        studentRepository.insertStudent(students);
        courseRepository.insertCourses(courses);
        Map<Student, Set<Course>> assignMap = new HashMap<>();
        Set<Course> courseSet = new HashSet<>(courses);
        assignMap.put(students.get(0), courseSet);
        studentRepository.assignToCourses(assignMap);

        assertEquals(courses, studentRepository.getStudentAssignments(students.get(0)));
    }

}
