package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private Connection connection;

    public UserDaoJDBCImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createUsersTable() {
        String query = """
                CREATE TABLE IF NOT EXISTS `users` (
                `id` BIGINT NOT NULL AUTO_INCREMENT,
                `name` varchar(45) NOT NULL,
                `last_name` varchar(45) NOT NULL,
                `age` int NOT NULL,
                PRIMARY KEY (`id`)
                )
                """;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            System.out.println("Таблица users успешно создана");
        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблицы users: " + e.getMessage());
        }
    }

    @Override
    public void dropUsersTable() {
        String query = "DROP TABLE IF EXISTS users";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            System.out.println("Таблица users успешно удалена");
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении таблицы users: " + e.getMessage());
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        String query = "INSERT INTO users (name, last_name, age) VALUES (?, ?, ?)";

        try {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, lastName);
                preparedStatement.setByte(3, age);
                preparedStatement.executeUpdate();
                connection.commit();
                System.out.println("Пользователь " + name + " добавлен в таблицу users");
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Ошибка при добавлении пользователя: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка управления транзакцией: " + e.getMessage());
        }
    }

    @Override
    public void removeUserById(long id) {
        String query = "DELETE FROM users WHERE id = ?";

        try {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
                connection.commit();
                System.out.println("Пользователь с id = " + id + " удален");
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка управления транзакцией: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString("name"),
                        resultSet.getString("last_name"),
                        resultSet.getByte("age"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка пользователей: " + e.getMessage());
        }
        return users;
    }


    @Override
    public void cleanUsersTable() {
        String query = "TRUNCATE TABLE users";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            System.out.println("Таблица users очищена");
        } catch (SQLException e) {
            System.err.println("Ошибка при очистке таблицы users: " + e.getMessage());
        }
    }
}
