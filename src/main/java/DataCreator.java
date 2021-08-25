import DAO.CourseRepository;
import DAO.Exceptions.DAOException;
import DAO.FileReader;
import DAO.GroupRepository;
import DAO.Models.Course;
import DAO.Models.Group;
import DAO.Models.Student;
import DAO.StudentRepository;

import java.util.*;

public class DataCreator {
    private FileReader dataReader = FileReader.getInstance();
    private Random random = new Random();
    private DataContainer container = new DataContainer();
    private GroupRepository groupDAO = new GroupRepository();
    private StudentRepository studentDAO = new StudentRepository();
    private CourseRepository courseDAO = new CourseRepository();

    public DataCreator() throws DAOException {
    }

    public DataContainer createTestData() throws DAOException {
        container.setGroups(createGroups());
        insertGroupsIntoDatabase();
        container.setCourses(createCourses());
        container.setStudents(createStudents());
        createGroupsAssignments();
        insertIntoDatabase();
        return container;
    }

    private List<Group> createGroups(){
        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            groups.add(new Group(createGroupName()));
        }
        return groups;
    }

    private List<Course> createCourses() throws DAOException {
        List<Course> courses = dataReader.getCourses("CoursesNames");
        int i = 1;
        for (Course course : courses){
            course.setId(i++);
        }
        return courses;
    }

    private String createGroupName(){
        StringBuilder name = new StringBuilder();
        name.append((char) (random.nextInt(26)+97));
        name.append((char) (random.nextInt(26)+97));
        name.append('-');
        name.append(random.nextInt(10));
        name.append(random.nextInt(10));
        return name.toString();
    }

    private Map<Student, Set<Course>> createStudents() throws DAOException {
        Map<Student, Set<Course>> students = new HashMap<>();
        List<String> firstNames = dataReader.getNames("FirstNames");
        List<String> lastNames = dataReader.getNames("LastNames");
        for(int i = 0;i<=200;i++){
            Student student = new Student(i+1, firstNames.get(random.nextInt(firstNames.size())), lastNames.get(random.nextInt(lastNames.size())));
            students.put(student, createCourseAssignments());
        }
        return students;
    }

    private Set<Course> createCourseAssignments() throws DAOException {
        int counter = random.nextInt(2) + 1;
        List<Course> courses = container.getCourses();
        Set<Course> courseAssignments = new HashSet<>();
        for(int i = 0; i <= counter; i++){
            courseAssignments.add(courses.get(random.nextInt(courses.size())));
        }
        return courseAssignments;
    }

    private void createGroupsAssignments() throws DAOException{
        List<Student> freeStudents = new ArrayList<>(container.getStudents().keySet());
        List<Group> groups = container.getGroups();
        for (Group g : groups){
            int counter = random.nextInt(2) * (random.nextInt(21) + 10);
            while (counter > 0) {
                Student student = freeStudents.get(random.nextInt(freeStudents.size()));
                student.setGroupId(g.getId());
                freeStudents.remove(student);
                counter--;
            }
        }
    }

    private void insertGroupsIntoDatabase() throws DAOException {
        groupDAO.insertGroup(container.getGroups());
    }

    private void insertIntoDatabase() throws DAOException {
        insertCoursesIntoDatabase();
        insertStudentsIntoDatabase();
        insertCourseAssignmentsIntoDatabase();
    }

    private void insertCoursesIntoDatabase() throws DAOException {
        courseDAO.insertCourses(container.getCourses());
    }

    private void insertStudentsIntoDatabase() throws DAOException {
        List<Student> students = new ArrayList<>(container.getStudents().keySet());
        studentDAO.insertStudent(students);
    }

    private void insertCourseAssignmentsIntoDatabase() throws DAOException {
        studentDAO.assignToCourses(container.getStudents());
    }
}
