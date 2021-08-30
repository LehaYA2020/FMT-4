import dao.*;
import dao.Exceptions.DAOException;
import models.Group;
import models.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GroupRepoTest {
    private static DBConnection dbConnection;
    private GroupRepository groupRepository= new GroupRepository();
    private StudentRepository studentRepository = new StudentRepository();
    private static List<Group> testGroupsList = new ArrayList<>();
    private static List<Student> testStudentList = new ArrayList<>();

    public GroupRepoTest() throws DAOException {
    }
    @BeforeAll
    public static void prepare() throws DAOException {
        dbConnection = dbConnection.getInstance("TestDatabaseH2.properties");
    }

    @BeforeEach
    public void createTables() throws DAOException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        scriptExecutor.executeScript("CreateTables.sql");
        for (int i = 1; i <= 3; i++){
            testGroupsList.add(new Group("te-st" + i));
        }
        for (int i = 1; i <= 10; i++){
            testStudentList.add(new Student(i, "Student-" + i,"forTest"));
        }
        insertStudentsToGroups();
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

    private void insertStudentsToGroups(){
        for (int i = 0; i<2;i++){
            testStudentList.get(i).setGroupId(testGroupsList.get(0).getId());
        }
        for (int i = 2; i<5;i++){
            testStudentList.get(i).setGroupId(testGroupsList.get(1).getId());
        }
        for (int i = 5; i<9;i++){
            testStudentList.get(i).setGroupId(testGroupsList.get(2).getId());
        }
    }
}