import dao.CourseRepository;
import dao.exceptions.DAOException;
import dao.FileReader;
import dao.GroupRepository;
import models.Course;
import models.Group;
import models.Student;
import dao.StudentRepository;

import java.util.*;

public class DataCreator {
    private final FileReader dataReader = FileReader.getInstance();
    private final Random random = new Random();
    private final DataContainer container = new DataContainer();
    private final GroupRepository groupDAO = new GroupRepository();
    private final StudentRepository studentDAO = new StudentRepository();
    private final CourseRepository courseDAO = new CourseRepository();

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
        String name = String.valueOf((char) (random.nextInt(26) + 97)) +
                (char) (random.nextInt(26) + 97) +
                '-' +
                random.nextInt(10) +
                random.nextInt(10);
        return name;
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
