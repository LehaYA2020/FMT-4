import dao.DBAccess;
import dao.Exceptions.DAOException;
import dao.FileReader;
import models.Course;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileReaderTest {
    private final FileReader fileReader = FileReader.getInstance();
    private final DBAccess ACCESS_WITHOUT_PASSWORD = new DBAccess("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            "sa", "");
    private final DBAccess ACCESS_WITH_PASSWORD = new DBAccess("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            "sa", "228"
    );
    private final String QUERY = "INSERT INTO courses(name, description) VALUES(?, ?);";
    private static final String[] expectedScripts = new String[3];
    private static final List<String> expectedNames = new ArrayList<>();
    private static final List<Course> expectedCourses = new ArrayList<>();

    @BeforeAll
    public static void prepare() throws DAOException {
        for (int i = 0; i < 3; i++) {
            expectedNames.add("testName" + i);
            expectedScripts[i] = "Test script " + i;
            expectedCourses.add(new Course("Test Course " + i, "Course for testing"));
        }
    }

    @Test
    public void getAccess_shouldReturnAccessWithoutPassword() {
        assertEquals(ACCESS_WITHOUT_PASSWORD, fileReader.getAccess("TestDatabaseH2.properties"));
    }

    @Test
    public void getAccess_shouldReturnAccessWithPassword() {
        assertEquals(ACCESS_WITH_PASSWORD, fileReader.getAccess("h2TestDatabase.properties"));
    }

    @Test
    public void getCourses_shouldReturnCourses() throws DAOException {
        List<Course> actualCourses = fileReader.getCourses("TestCourses");
        assertEquals(expectedCourses, actualCourses);
    }

    @Test
    public void shouldReturnNames() throws DAOException {
        List<String> actualNames = fileReader.getNames("TestNames");
        assertEquals(expectedNames, actualNames);
    }

    @Test
    public void shouldReturnScripts() throws DAOException {
        String[] actual = fileReader.getScript("TestScript");
        assertArrayEquals(expectedScripts, actual);
    }
}
