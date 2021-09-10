package dao;

import dao.Exceptions.DAOException;
import dao.Exceptions.MessagesConstants;
import models.Course;
import models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StudentRepository {
    private final String GET_ALL_STUDENTS = "SELECT students.id, students.first_name, students.last_name, students_groups.group_id FROM students LEFT JOIN students_groups on students_groups.student_id = students.id;";
    private final String GET_STUDENT_BY_ID = "SELECT students.id, students.first_name, students.last_name, students_groups.group_id FROM students LEFT JOIN students_groups on students_groups.student_id = students.id WHERE students.id = ?;";
    private final String DELETE_STUDENT = "DELETE FROM students WHERE id = ?;";
    private final String DELETE_STUDENT_FROM_COURSE = "delete FROM students_courses where student_id = ? AND course_id = ?;";
    private final String ASSIGN_TO_COURSE = "INSERT INTO students_courses(student_id, course_id) VALUES(?, ?);";
    private final String GET_STUDENT_ASSIGNMENTS = "SELECT course_id FROM students_courses WHERE student_id = ?;";
    private final String GET_STUDENTS_BY_COURSE_NAME = "SELECT students.id, students.first_name, students.last_name, students_groups.group_id FROM students INNER JOIN students_courses ON students.id = students_courses.student_id LEFT JOIN students_groups ON students_groups.student_id = students.id INNER JOIN courses ON courses.id = students_courses.course_id WHERE courses.name = ?;";
    private final String INSERT_STUDENT = "INSERT INTO students(first_name, last_name) VALUES(?, ?);";
    private final String INSERT_STUDENT_TO_COURSE = "INSERT INTO students_groups(student_id, group_id) VALUES(?, ?);";

    private final DBConnection dbConnection = DBConnection.getInstance();

    public List<Student> insertStudent(List<Student> students) throws DAOException {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT, Statement.RETURN_GENERATED_KEYS)) {
            for (Student s : students) {
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
        return students;
    }

    public List<Student> getAllStudents() throws DAOException {
        List<Student> students;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_STUDENTS); ResultSet resultSet = preparedStatement.executeQuery()) {
            students = processStudentsSet(resultSet);
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_ALL_STUDENTS, e);
        }
        return students;
    }

    public Student getStudentById(int id) throws DAOException {
        List<Student> students;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_STUDENT_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                students = processStudentsSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_STUDENT_BY_ID, e);
        }
        return students.get(0);
    }

    public void deleteStudent(Student student) throws DAOException {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_STUDENT)) {
            preparedStatement.setInt(1, student.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_DELETE_STUDENT, e);
        }
    }

    public List<Student> getStudentsByCourseName(String courseName) throws DAOException {
        List<Student> students;
        try (Connection connection = dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(GET_STUDENTS_BY_COURSE_NAME)) {
            preparedStatement.setString(1, courseName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                students = processStudentsSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_STUDENTS_BY_COURSE, e);
        }
        return students;
    }

    public boolean assignToCourse(Student student, Course course) throws DAOException {
        boolean flag = false;
        try (Connection connection = dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ASSIGN_TO_COURSE)) {
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
        List<Course> courses;
        try (Connection connection = dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(GET_STUDENT_ASSIGNMENTS)) {
            preparedStatement.setInt(1, student.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                courses = processAssignmentsSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_ASSIGNMENTS, e);
        }
        return courses;
    }

    public Map<Student, Set<Course>> assignToCourses(Map<Student, Set<Course>> assignStudents) throws DAOException {
        try (Connection connection = dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ASSIGN_TO_COURSE)) {
            for (Map.Entry<Student, Set<Course>> entry : assignStudents.entrySet()) {
                Student student = entry.getKey();
                List<Course> courses = this.getStudentAssignments(student);
                for (Course course : entry.getValue()) {
                    if (!courses.contains(course)) {
                        preparedStatement.setInt(1, student.getId());
                        preparedStatement.setInt(2, course.getId());
                        preparedStatement.addBatch();
                    }
                }
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_ASSIGN_TO_COURSES, e);
        }
        return assignStudents;
    }

    public void deleteStudentFromCourse(Student student, Course course) throws DAOException {
        try (Connection connection = dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(DELETE_STUDENT_FROM_COURSE)) {
            preparedStatement.setInt(1, student.getId());
            preparedStatement.setInt(2, course.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_DELETE_FROM_COURSE, e);
        }
    }

    private void insertStudentToGroup(List<Student> students) throws DAOException {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT_TO_COURSE)) {
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
            while (resultSet.next()) {
                int courseID = resultSet.getInt("course_id");
                courses.add(new CourseRepository().getCourseById(courseID));
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_PROCESS_ASSIGNMENTS_SET, e);
        }
        return courses;
    }
}
