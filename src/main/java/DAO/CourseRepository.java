package DAO;

import DAO.Exceptions.DAOException;
import DAO.Exceptions.MessagesConstants;
import DAO.Models.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {
    private final DBConnection dbConnection = DBConnection.getInstance();
    private final FileReader fileReader = FileReader.getInstance();

    public CourseRepository() throws DAOException {
    }

    public List<Course> getAllCourses() throws DAOException {
        String Query = fileReader.getQuery("getAllCourses.sql");
        List<Course> courses;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            courses = processCoursesSet(resultSet);
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_COURSES, e);
        }
        return courses;
    }

    public Course getCourseById(int id) throws DAOException {
        String Query = fileReader.getQuery("getCourseById.sql");
        List<Course> courses;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                courses = processCoursesSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_COURSE_BY_ID, e);
        }
        return courses.get(0);
    }

    public void insertCourses(List<Course> courses) throws DAOException {
        String Query = fileReader.getQuery("insertCourses.sql");
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS)) {
            for (Course c : courses) {
                preparedStatement.setString(1, c.getName());
                preparedStatement.setString(2, c.getDescription());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                for (Course course : courses) {
                    if (resultSet.next()) {
                        course.setId(resultSet.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_INSERT_COURSES, e);
        }

    }

    private List<Course> processCoursesSet(ResultSet resultSet) throws DAOException {
        List<Course> courses = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Course course = new Course(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("description"));
                courses.add(course);
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_PROCESS_COURSES_SET, e);
        }
        return courses;
    }
}
