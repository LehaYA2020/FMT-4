package DAO;

import DAO.Exceptions.DAOException;
import DAO.Exceptions.MessagesConstants;
import DAO.Models.Course;
import DAO.Models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StudentsRepository {
    private final DBConnection dbConnection = DBConnection.getInstance();
    private final FileReader fileReader = FileReader.getInstance();

    public StudentsRepository() throws DAOException {
    }

    public void insertStudent(List<Student> students) throws DAOException {
        String Query = fileReader.getQuery("insertStudent.sql");
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS)){
            for (Student s : students){
                preparedStatement.setString(1, s.getFirstName());
                preparedStatement.setString(2, s.getLastName());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                for (Student student : students) {
                    if (generatedKeys.next()) {
                        student.setId(generatedKeys.getInt(1));
                    }
                }
            }
            insertStudentToGroup(students);
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_STUDENT_BY_ID, e);
        }
    }

    private void insertStudentToGroup(List<Student> students) throws DAOException {
        String query = fileReader.getQuery("insertStudentToGroup.sql");
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Student student : students) {
                if (student.getGroupId() > 0) {
                    preparedStatement.setInt(1, student.getId());
                    preparedStatement.setInt(2, student.getGroupId());
                    preparedStatement.addBatch();
                }
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_INSERT_STUDENTS_LIST, e);
        }
    }

    public List<Student> getAllStudents() throws DAOException {
        String Query = fileReader.getQuery("getAllStudents.sql");
        List<Student> students;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Query); ResultSet resultSet = preparedStatement.executeQuery()) {
            students = processStudentsSet(resultSet);
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_ALL_STUDENTS, e);
        }
        return students;
    }

    public Student getStudentById(int id) throws DAOException {
        String Query = fileReader.getQuery("getStudentById.sql");
        List<Student> students;
        try(Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(Query)){
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                students = processStudentsSet(resultSet);
            }
        }catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_STUDENT_BY_ID , e);
        }
        return students.get(0);
    }

    public void deleteStudent(Student student) throws DAOException {
        String Query = fileReader.getQuery("deleteStudentById.sql");
        try(Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(Query)){
            preparedStatement.setInt(1,student.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_DELETE_STUDENT, e);
        }
    }

    public List<Student> getStudentsByCourseName(String courseName) throws DAOException {
        String Query = fileReader.getQuery("getStudentsByCourseName.sql");
        List<Student> students;
        try (Connection connection = dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Query)){
            preparedStatement.setString(1, courseName);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                students = processStudentsSet(resultSet);
            }
        }catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_STUDENTS_BY_COURSE, e);
        }
        return students;
    }

    public boolean assignToCourse(Student student, Course course) throws DAOException {
        String Query = fileReader.getQuery("assignToCourse.sql");
        boolean flag = false;
        try (Connection connection = dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Query)) {
            List<Course> courses = this.getStudentAssignments(student);
            if (!courses.contains(course)) {
                preparedStatement.setInt(1, student.getId());
                preparedStatement.setInt(2, course.getId());
                preparedStatement.executeUpdate();
                flag = true;
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_ASSIGN_TO_COURSE, e);
        }
        return flag;
    }

    public List<Course> getStudentAssignments(Student student) throws DAOException {
        String Query = fileReader.getQuery("getStudentAssignments.sql");
        List<Course> courses;
        try(Connection connection = dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Query)){
            preparedStatement.setInt(1, student.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                courses = processAssignmentsSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_ASSIGNMENTS, e);
        }
        return courses;
    }

    public void assignToCourses(Map<Student, Set<Course>> assignStudents) throws DAOException {
        String Query = fileReader.getQuery("assignToCourse.sql");
        try (Connection connection = dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Query)) {
            for (Map.Entry<Student, Set<Course>> entry: assignStudents.entrySet()){
                Student student = entry.getKey();
                List<Course> courses = this.getStudentAssignments(student);
                for (Course course : entry.getValue()){
                    if(!courses.contains(course)){
                        preparedStatement.setInt(1, student.getId());
                        preparedStatement.setInt(2, course.getId());
                        preparedStatement.addBatch();
                    }
                }
            }
            preparedStatement.executeBatch();
        } catch (SQLException e){
            throw new DAOException(MessagesConstants.CANNOT_ASSIGN_TO_COURSES, e);
        }
    }

    public void deleteStudentFromCourse(Student student, Course course) throws DAOException {
        String Query = fileReader.getQuery("deleteStudentFromCourse.sql");
        try (Connection connection = dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Query)){
            preparedStatement.setInt(1, student.getId());
            preparedStatement.setInt(2, course.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_DELETE_FROM_COURSE, e);
        }
    }

    private List<Student> processStudentsSet(ResultSet resultSet) throws DAOException {
        List<Student> students = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Student student = new Student(resultSet.getInt("id"), resultSet.getString("first_name"),
                        resultSet.getString("last_name"));
                student.setGroupId(resultSet.getInt("group_id"));
                students.add(student);
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_PROCESS_STUDENTS_SET, e);
        }
        return students;
    }

    private List<Course> processAssignmentsSet(ResultSet resultSet) throws DAOException {
        List<Course> courses = new ArrayList<>();
        try {
            while (resultSet.next()){
                Integer courseID = resultSet.getInt("course_id");
                courses.add(new CourseRepository().getCourseById(courseID));
            }
        }catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_PROCESS_ASSIGNMENTS_SET, e);
        }
        return courses;
    }
}
