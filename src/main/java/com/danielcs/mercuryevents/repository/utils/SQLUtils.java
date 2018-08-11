package com.danielcs.mercuryevents.repository.utils;

import java.sql.*;
import java.util.*;

public class SQLUtils {

    private Connection connection;

    public SQLUtils(String path, String username, String password) {
        try {
            connection = DriverManager.getConnection(path, username, password);
        } catch (SQLException e) {
            System.out.println("Could not establish database connection.");
            e.printStackTrace();
        }
    }

    public void executeUpdate(String queryLine, Object... params) {
        try (PreparedStatement statement = connection.prepareStatement(queryLine)){

            setParameters(statement, params);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.printf(
                    "Database Error occurred when trying to execute a mutation. Details:\nQuery: %s\nParams: %s",
                    queryLine, Arrays.toString(params)
            );
        }
    }

    public <T> T fetchOne(String queryLine, ModelAssembler<T> assembler, Object... params) {
        try (PreparedStatement statement = connection.prepareStatement(queryLine)) {

            setParameters(statement, params);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return assembler.assemble(rs);
            }
            return null;

        } catch (SQLException e) {
            System.out.printf(
                    "Database Error occurred when trying to fetch an entry. Details:\nQuery: %s\nParams: %s",
                    queryLine, Arrays.toString(params)
            );
        }
        return null;
    }

    public <T> List<T> fetchAll(String queryLine, ModelAssembler<T> assembler, String... params) {
        try (PreparedStatement statement = connection.prepareStatement(queryLine)) {

            setParameters(statement, params);
            ResultSet rs = statement.executeQuery();
            List<T> results = new LinkedList<>();
            while (rs.next()) {
                T newObject = assembler.assemble(rs);
                results.add(newObject);
            }
            return results;

        } catch (SQLException e) {
            System.out.printf(
                    "Database Error occurred when trying to fetch multiple entries. Details:\nQuery: %s\nParams: %s",
                    queryLine, Arrays.toString(params)
            );
        }
        return null;
    }

    private void setParameters(PreparedStatement statement, Object[] params) throws SQLException {
        int counter = 1;
        for (Object param : params) {
            statement.setObject(counter++, param);
        }
    }
}
