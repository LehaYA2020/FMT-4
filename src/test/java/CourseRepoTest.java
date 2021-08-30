import dao.CourseRepository;
import dao.*;
import dao.Exceptions.DAOException;
import models.Course;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class CourseRepoTest {
    private CourseRepository courseRepository = new CourseRepository();
    private static List<Course> testCourseList = new ArrayList<>();
    private static DBConnection dbConnection;

    @BeforeEach
    public void createTables() throws DAOException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        scriptExecutor.executeScript("CreateTables.sql");
        for (int i = 1; i <= 3; i++){
            testCourseList.add(new Course("Course-" + i, "forTest"));
        }
    }

    @BeforeAll
    public static void prepare() throws DAOException {
        dbConnection = dbConnection.getInstance("TestDatabaseH2.properties");
    }

    @Test
    public void shouldInsertCoursesIntoDB() throws DAOException {
        courseRepository.insertCourses(testCourseList);

        assertEquals(testCourseList, courseRepository.getAllCourses());
    }

    @Test
    public void shouldGetAllCoursesFromDB() throws DAOException {
        courseRepository.insertCourses(testCourseList);
        List<Course> actualCourses = courseRepository.getAllCourses();
        assertEquals(testCourseList, actualCourses);
    }

    @Test
    public void shouldGetCourseById() throws DAOException {
        courseRepository.insertCourses(testCourseList);
        Course actual = courseRepository.getCourseById(1);
        Course expected = testCourseList.get(0);

        assertEquals(expected, actual);
    }
}
