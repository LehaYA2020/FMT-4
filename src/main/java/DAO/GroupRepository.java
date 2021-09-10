package dao;

import dao.Exceptions.DAOException;
import dao.Exceptions.MessagesConstants;
import models.Group;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupRepository {
    private final String GET_ALL_GROUPS = "SELECT * FROM groups;";
    private final String GET_GROUPS_BY_COUNTER = "SELECT groups.id, groups.name, COUNT(*) FROM groups LEFT JOIN students_groups on students_groups.group_id = groups.id GROUP BY groups.id HAVING COUNT(*) <= ?;";
    private final String INSERT_GROUP = "INSERT INTO groups(name) VALUES (?);";

    private final DBConnection dbConnection = DBConnection.getInstance();


    public List<Group> insertGroup(List<Group> groups) throws DAOException {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(INSERT_GROUP, Statement.RETURN_GENERATED_KEYS)) {
            for (Group group : groups) {
                preparedStatement.setString(1, group.getName());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                for (Group group : groups) {
                    if (resultSet.next()) {
                        group.setId(resultSet.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_INSERT_GROUPS, e);
        }
        return groups;
    }

    public List<Group> getAllGroups() throws DAOException {
        List<Group> groups;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ALL_GROUPS);
             ResultSet resultSet = statement.executeQuery()) {
            groups = processGroupSet(resultSet);
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_GROUPS, e);
        }
        return groups;
    }

    public List<Group> getGroupsByCounter(int counter) throws DAOException {
        List<Group> groups;

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_GROUPS_BY_COUNTER)) {
            preparedStatement.setInt(1, counter);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                groups = processGroupSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_GET_GROUPS_BY_COUNTER, e);
        }
        return groups;
    }

    private List<Group> processGroupSet(ResultSet resultSet) throws DAOException {
        List<Group> groups = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Group group = new Group(resultSet.getInt("id"), resultSet.getString("name"));
                groups.add(group);
            }
        } catch (SQLException e) {
            throw new DAOException(MessagesConstants.CANNOT_PROCESS_GROUP_SET, e);
        }
        return groups;
    }
}
