import dao.exceptions.DAOException;
import dao.GroupRepository;
import dao.ScriptExecutor;
import dao.StudentRepository;
import models.Group;
import models.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupRepoTest {
    private final GroupRepository groupRepository = new GroupRepository();
    private final StudentRepository studentRepository = new StudentRepository();
    private static final List<Group> testGroupsList = new ArrayList<>();
    private static final List<Student> testStudentList = new ArrayList<>();

    @BeforeAll
    public static void prepare() {
        for (int i = 1; i <= 3; i++) {
            testGroupsList.add(new Group("te-st" + i));
        }
        for (int i = 1; i <= 10; i++) {
            testStudentList.add(new Student(i, "Student-" + i, "forTest"));
        }
        insertStudentsToGroups();
    }

    @BeforeEach
    public void createTables() throws DAOException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        scriptExecutor.executeScript("CreateTables.sql");
    }

    @Test
    public void shouldInsertGroupsIntoDB() throws DAOException {
        groupRepository.insertGroup(testGroupsList);

        assertEquals(testGroupsList, groupRepository.getAllGroups());
    }

    @Test
    public void shouldGetAllGroupsFromDB() throws DAOException {
        groupRepository.insertGroup(testGroupsList);

        assertEquals(testGroupsList, groupRepository.getAllGroups());
    }

    @Test
    public void shouldGetGroupsByCounter() throws DAOException {
        groupRepository.insertGroup(testGroupsList);
        studentRepository.insertStudent(testStudentList);

        List<Group> expectedGroups = new ArrayList<>();
        expectedGroups.add(testGroupsList.get(0));
        expectedGroups.add(testGroupsList.get(1));
        expectedGroups.add(testGroupsList.get(2));

        List<Group> actualGroups = groupRepository.getGroupsByCounter(3);

        assertEquals(expectedGroups, actualGroups);
    }

    private static void insertStudentsToGroups() {
        for (int i = 0; i < 9; i++) {
            if (i<2)
            testStudentList.get(i).setGroupId(testGroupsList.get(0).getId());
            else if (i<5 && i>2)
                testStudentList.get(i).setGroupId(testGroupsList.get(1).getId());
            else testStudentList.get(i).setGroupId(testGroupsList.get(2).getId());
        }
    }
}
