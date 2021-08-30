import dao.*;
import dao.Exceptions.DAOException;
import models.Course;
import models.Group;
import models.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataCreatorTest {
    private final DataCreator dataCreator = new DataCreator();
    private static DBConnection dbConnection;
    private final StudentRepository studentRepository = new StudentRepository();
    private final CourseRepository courseRepository = new CourseRepository();
    private final GroupRepository groupRepository = new GroupRepository();
    private DataContainer dataContainer;

    public DataCreatorTest() throws DAOException {
    }

    @BeforeAll
    public static void prepare() throws DAOException {
        dbConnection = DBConnection.getInstance("TestDatabaseH2.properties");
    }

    @BeforeEach
    public void createTables() throws DAOException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        scriptExecutor.executeScript("CreateTables.sql");
    }

    @Test
    public void createTestData_shouldCreateDataAndPutItToDB() throws DAOException {
        List<Student> students = studentRepository.getAllStudents();
        List<Course> courses = courseRepository.getAllCourses();
        List<Group> groups = groupRepository.getAllGroups();

        dataContainer = dataCreator.createTestData();

        students = studentRepository.getAllStudents();
        assertEquals(dataContainer.getStudents().keySet().size(), students.size());
        assertTrue(students.containsAll(dataContainer.getStudents().keySet()));

        courses = courseRepository.getAllCourses();
        assertEquals(dataContainer.getCourses().size(), courses.size());
        assertTrue(courses.containsAll(dataContainer.getCourses()));

        groups = groupRepository.getAllGroups();
        assertEquals(dataContainer.getGroups().size(), groups.size());
        assertTrue(groups.containsAll(dataContainer.getGroups()));
    }

    @Test
    public void createTestData_groupShouldContainZeroOrTenToThirtyStudents() throws DAOException {
        DataCreator dataCreator = new DataCreator();
        dataContainer = dataCreator.createTestData();

        List<Student> students = studentRepository.getAllStudents();
        Map<Integer, Integer> groupsCounter = new HashMap<>();
        for (Student student : students) {
            Integer groupId = student.getGroupId();
            if (groupId > 0) {
                groupsCounter.put(groupId, groupsCounter.getOrDefault(groupId, 0) + 1);
            }
        }
        for (Integer groupId : groupsCounter.keySet()) {
            Integer count = groupsCounter.get(groupId);
            assertTrue(count >= 10 && count <= 30);
        }
    }
}
