import dao.CourseRepository;
import dao.exceptions.DAOException;
import dao.GroupRepository;
import dao.ScriptExecutor;
import dao.StudentRepository;
import models.Course;
import models.Group;
import models.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataCreatorTest {
    private final DataCreator dataCreator = new DataCreator();
    private final StudentRepository studentRepository = new StudentRepository();
    private final CourseRepository courseRepository = new CourseRepository();
    private final GroupRepository groupRepository = new GroupRepository();
    private DataContainer dataContainer;

    @BeforeEach
    public void createTables() throws DAOException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        scriptExecutor.executeScript("CreateTables.sql");
    }

    @Test
    public void createTestData_shouldCreateDataAndPutItToDB() throws DAOException {
        List<Student> students;
        List<Course> courses;
        List<Group> groups;

        dataContainer = dataCreator.createTestData();

        students = studentRepository.getAllStudents();
        assertEquals(dataContainer.getStudents().keySet().size(), students.size());
        for (int i = 0; i<students.size()-1; i++) {
            assertTrue(students.containsAll(dataContainer.getStudents().keySet()));
        }

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
