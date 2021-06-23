package com.akvelon.planningsystem.repo;

import com.akvelon.planningsystem.entity.Status;
import com.akvelon.planningsystem.entity.Task;
import com.akvelon.planningsystem.exception.DaoOperationException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.util.*;

//@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private static final String INSERT_SQL = "INSERT INTO tasks(name, description, creation_date, status, priority) VALUES (?, ?, ?, ?,?);";
    private static final String SELECT_ALL_SQL = "SELECT * FROM tasks;";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM tasks WHERE id = ?;";
    private static final String UPDATE_BY_ID_SLQ = "UPDATE tasks SET name = ?, description = ?, creation_date = ?, status = ?, priority = ? WHERE id = ?;";
    private static final String REMOVE_BY_ID_SQL = "DELETE FROM tasks WHERE id = ?;";
//    private static final String SORT_BY = "SELECT * FROM tasks ORDER BY " + sortCol.get("keySort");

    private DataSource dataSource;
    static Map<String, String> sortCol;

    {
        sortCol = new HashMap<String, String>() {
            {
                put("prior", "priority");
                put("creation", "creation_date");
                put("stat", "status");
                put("id_task", "id");
            }

            @Override
            public String get(Object key) {
                String col = super.get(key);
                return null == col ? "priority" : col;
            }
        };
    }


    public TaskRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Task task) {
        Objects.requireNonNull(task);
        try (Connection connection = dataSource.getConnection()) {
            saveTask(task, connection);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Error saving task: %s", task), e);
        }
    }

    private void saveTask(Task task, Connection connection) throws SQLException {
        PreparedStatement insertStatement = prepareInsertStatement(task, connection);
        insertStatement.executeUpdate();
        Long id = fetchGeneratedId(insertStatement);
        task.setId(id);
    }

    private PreparedStatement prepareInsertStatement(Task task, Connection connection) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(INSERT_SQL,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            fillTaskStatement(task, insertStatement);
            return insertStatement;
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot prepare statement for task: %s", task), e);
        }
    }

    private void fillTaskStatement(Task task, PreparedStatement updateStatement) throws SQLException {
        updateStatement.setString(1, task.getName());
        updateStatement.setString(2, task.getDescription());
        updateStatement.setDate(3, Date.valueOf(task.getCreatedDate()));
        updateStatement.setString(4, String.valueOf(task.getStatus()));
        updateStatement.setInt(5, task.getPriority());
    }

    private Long fetchGeneratedId(PreparedStatement insertStatement) throws SQLException {
        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        } else {
            throw new DaoOperationException("Can not obtain task ID");
        }
    }

    @Override
    public List<Task> findAll() {
        try (Connection connection = dataSource.getConnection()) {
            return findAllTasks(connection);
        } catch (SQLException e) {
            throw new DaoOperationException("Error finding all tasks", e);
        }
    }

    private List<Task> findAllTasks(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_SQL);
        return collectToList(resultSet);
    }

    private List<Task> collectToList(ResultSet resultSet) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        while (resultSet.next()) {
            Task task = parseRow(resultSet);
            tasks.add(task);
        }
        return tasks;
    }

    private Task parseRow(ResultSet resultSet) {
        try {
            return createFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DaoOperationException("Cannot parse row to create task instance", e);
        }
    }

    private Task createFromResultSet(ResultSet resultSet) throws SQLException {
        Task task = new Task();
        task.setId(resultSet.getLong("id"));
        task.setName(resultSet.getString("name"));
        task.setDescription(resultSet.getString("description"));
        task.setCreatedDate(resultSet.getTimestamp("creation_date").toLocalDateTime().toLocalDate());
        task.setStatus(Status.valueOf(resultSet.getString("status")));
        task.setPriority(resultSet.getInt("priority"));
        return task;
    }

    @Override
    public Task findOne(Long id) {
        Objects.requireNonNull(id);
        try (Connection connection = dataSource.getConnection()) {
            return findTaskById(id, connection);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Error finding task by id = %d", id), e);
        }
    }

    private Task findTaskById(Long id, Connection connection) throws SQLException {
        PreparedStatement selectByIdStatement = prepareSelectByIdStatement(id, connection);
        ResultSet resultSet = selectByIdStatement.executeQuery();
        if (resultSet.next()) {
            return parseRow(resultSet);
        } else {
            throw new DaoOperationException(String.format("Task with id = %d does not exist", id));
        }
    }

    private PreparedStatement prepareSelectByIdStatement(Long id, Connection connection) {
        try {
            PreparedStatement selectByIdStatement = connection.prepareStatement(SELECT_BY_ID_SQL);
            selectByIdStatement.setLong(1, id);
            return selectByIdStatement;
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot prepare select by id statement for id = %d", id), e);
        }
    }

    @Override
    public void update(Task task) {
        Objects.requireNonNull(task);
        try (Connection connection = dataSource.getConnection()) {
            updateTask(task, connection);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Error updating task: %s", task), e);
        }
    }


    private void updateTask(Task task, Connection connection) throws SQLException {
        checkIdIsNotNull(task);
        PreparedStatement updateStatement = prepareUpdateStatement(task, connection);
        executeUpdateById(updateStatement, task.getId());
    }

    private void executeUpdateById(PreparedStatement insertStatement, Long taskId) throws SQLException {
        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected == 0) {
            throw new DaoOperationException(String.format("Task with id = %d does not exist", taskId));
        }
    }

    private PreparedStatement prepareUpdateStatement(Task task, Connection connection) {
        try {
            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_BY_ID_SLQ);
            fillTaskStatement(task, updateStatement);
            updateStatement.setLong(5, task.getId());
            return updateStatement;
        } catch (Exception e) {
            throw new DaoOperationException(String.format("Cannot prepare update statement for task: %s", task), e);
        }
    }

    @Override
    public void remove(Task task) {
        Objects.requireNonNull(task);
        try (Connection connection = dataSource.getConnection()) {
            removeTask(task, connection);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Error removing task by id = %d", task.getId()), e);
        }
    }

    private void removeTask(Task task, Connection connection) throws SQLException {
        checkIdIsNotNull(task);
        PreparedStatement removeStatement = prepareRemoveStatement(task, connection);
        executeUpdateById(removeStatement, task.getId());
    }

    private PreparedStatement prepareRemoveStatement(Task task, Connection connection) {
        try {
            PreparedStatement removeStatement = connection.prepareStatement(REMOVE_BY_ID_SQL);
            removeStatement.setLong(1, task.getId());
            return removeStatement;
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot prepare statement for task: %s", task), e);
        }
    }

    @Override
    public List<Task> sortBy(String pref) {
        try (Connection connection = dataSource.getConnection()) {
            return findAllSortedTasksBy(connection, pref);
        } catch (SQLException e) {
            throw new DaoOperationException("Error finding all tasks", e);
        }
    }


    private List<Task> findAllSortedTasksBy(Connection connection, String pref) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM tasks ORDER BY " + sortCol.get(pref));
        return collectToList(resultSet);
    }


    private void checkIdIsNotNull(Task task) {
        if (task.getId() == null) {
            throw new DaoOperationException("Task id cannot be null");
        }
    }
}
