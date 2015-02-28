package com.github.otbproject.otbproject.database;

import com.github.otbproject.otbproject.App;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by justin on 23/12/2014.
 */
public class DatabaseWrapper {
    final Connection connection;

    /**
     * Private constructor, should never be used directly. <br>
     * Instead use <code>createDatabase()</code>.
     *
     * @param path The path to the database file, should already exist.
     * @param tables A HashMap of Table name to a HashSet of the field names.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private DatabaseWrapper(String path, HashMap<String, HashSet<String>> tables) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        for (String key : tables.keySet()) {
            createTable(key, tables.get(key));
        }
    }

    /**
     * Static method for creation of a DataBase Wrapper Object. <br>
     * Will return a new DataBaseWrapper Object or null if either an <code>SQLException</code> or a <code>CLassNotFoundException</code>.
     *
     * @param path The path to the database file, should already exist.
     * @param tables A HashMap of Table name to a HashSet of the field names.
     * @see com.github.otbproject.otbproject.database.DatabaseHelper
     * @return a new DataBaseWrapper Object or null if either an <code>SQLException</code> or a <code>CLassNotFoundException</code>.
     */
    public static DatabaseWrapper createDataBase(String path, HashMap<String, HashSet<String>> tables) {
        try {
            return new DatabaseWrapper(path, tables);
        } catch (SQLException e) {
            App.logger.catching(e);
            return null;
        } catch (ClassNotFoundException e) {
            App.logger.catching(e);
            return null;
        }
    }

    /**
     * Creates a table in the database with no primary key.
     *
     * @param name The name of the table to create.
     * @param table A HashSet of field names for the table.
     * @return False if an <code>SQLException</code> is thrown, else it returns true.
     */
    public boolean createTable(String name, HashSet<String> table) {
        return createTable(name, table, null);
    }

    /**
     *  Creates a table in the database with a primary key.
     *
     * @param name The name of the table to create.
     * @param table A HashSet of field names for the table.
     * @param primaryKey The field name for the primary key.
     * @return False if an <code>SQLException</code> is thrown, else it returns true.
     */
    public boolean createTable(String name, HashSet<String> table, String primaryKey) {
        PreparedStatement preparedStatement = null;
        String sql = "CREATE TABLE IF NOT EXISTS " + name + " (";
        for (String key : table) {
            if (key.equals(primaryKey)) {
                sql = sql + key + " PRIMARY KEY TEXT, ";
            } else {
                sql = sql + key + " TEXT, ";
            }
        }
        sql = sql.substring(0, sql.length() - 2) + ")";
        boolean bool = true;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
            bool = false;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.logger.catching(e);
                bool = false;
            }

        }
        return bool;
    }

    /**
     * Retrieves a <code>ResultSet</code> that contains all records that match the filter.
     * The Filter is defined as a field name and field value pair. <br>
     * i.e. CommandName and !test where CommandName is a field name and !test is a value in that field, <br>
     * this method would return a result set of all records that had !test in the CommandName field.
     *
     * @param table The table name.
     * @param identifier what the filter should match.
     * @param fieldName the field you want to filter with.
     * @return a <code>ResultSet</code> that contains the records that match the Identifier in the field specified.
     * @see java.sql.ResultSet
     */
    public ResultSet getRecord(String table, String identifier, String fieldName) {
        PreparedStatement preparedStatement = null;
        String sql = "SELECT * FROM " + table + " WHERE " + fieldName + "= ?";
        ResultSet rs = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, identifier);
            rs = preparedStatement.executeQuery();
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.logger.catching(e);
            }

        }
        return rs;
    }

    /**
     * Checks to see if a record with a Field Name, to Field Value pair exists in the table.
     *
     * @param table The table name.
     * @param identifier what the filter should match.
     * @param fieldName the field you want to filter with.
     * @return True if the identifier exists in the field in the table.
     */
    public boolean exists(String table, String identifier, String fieldName) {
        PreparedStatement preparedStatement = null;
        String sql = "SELECT " + fieldName + " FROM " + table + " WHERE " + fieldName + "= ?";
        boolean bool = false;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, identifier);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (rs.getString(fieldName).equals(identifier)) {
                    bool = true;
                }
            }
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
            bool = false;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                bool = false;
                App.logger.catching(e);
            }

        }
        return bool;
    }

    /**
     * Updates an existing record in the database.
     *
     * @param table The table name.
     * @param identifier what the filter should match.
     * @param fieldName the field you want to filter with.
     * @param map A HashMap of Field Name to Field Value.
     * @return True if one or more records were updated in the database.
     */
    public boolean updateRecord(String table, String identifier, String fieldName, HashMap<String, String> map) {
        PreparedStatement preparedStatement = null;
        String sql = "UPDATE " + table + " SET ";
        for (String key : map.keySet()) {
            sql += key + "=?, ";
        }
        sql = sql.substring(0, sql.length() - 2);
        sql += " WHERE " + fieldName + "= ?";
        boolean bool = false;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            int index = 1;
            for (String key : map.keySet()) {
                preparedStatement.setString(index, map.get(key));
                index++;
            }
            preparedStatement.setString(index, identifier);
            int i = preparedStatement.executeUpdate();
            if (i > 0) {
                bool = true;
            }
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
            bool = false;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.logger.catching(e);
                bool = false;
            }

        }
        return bool;
    }

    /**
     * Inserts a record into the Database.
     *
     * @param table The table name.
     * @param identifier what the filter should match.
     * @param fieldName the field you want to filter with.
     * @param map A HashMap of Field Name to Field Value.
     * @return True if the record was inserted into database.
     */
    public boolean insertRecord(String table, String identifier, String fieldName, HashMap<String, String> map) {
        PreparedStatement preparedStatement = null;
        String sql = "INSERT INTO " + table + " VALUES (";
        for (String key : map.keySet()) {
            sql += "?, ";
        }
        sql = sql.substring(0, sql.length() - 2) + ")";
        boolean bool = false;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            int index = 1;
            for (String key : map.keySet()) {
                preparedStatement.setString(index, map.get(key));
                index++;
            }
            int i = preparedStatement.executeUpdate();
            if (i > 0) {
                bool = true;
            }
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
            bool = false;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.logger.catching(e);
                bool = false;
            }
        }
        return bool;
    }

    /**
     * Removes a record from the Database.
     *
     * @param table The table name.
     * @param identifier what the filter should match.
     * @param fieldName the field you want to filter with.
     * @return True if one or more records are removed.
     */
    public boolean removeRecord(String table, String identifier, String fieldName) {
        PreparedStatement preparedStatement = null;
        String sql = "DELETE FROM " + table + " WHERE " + fieldName + "=?";
        boolean bool = false;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, identifier);
            int i = preparedStatement.executeUpdate();
            if (i > 0) {
                bool = true;
            }
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
            bool = false;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.logger.catching(e);
                bool = false;
            }
        }
        return bool;
    }

    /**
     * Retrieves all records from a table.
     *
     * @param table The Table name.
     * @return  result set that contains all records of the table.
     */
    public ResultSet tableDump(String table) {
        String sql = "SELECT * FROM " + table;
        try {
            return connection.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            App.logger.catching(e);
            return null;
        }

    }

    /**
     * Retrieves a list of entries in a singular field in a specific table.<br>
     * The list may contain duplicate elements, if the field specified is neither the <code>PRIMARY KEY</code>, or marked as <code>UNIQUE</code>.
     *
     * @param table The table name that you want the record list from.
     * @param key The field in the table you want to get.
     * @return an <code>ArrayList&lt;String&gt;</code> that contains all entries in the table specified for the field key or <code>null</code> if an <code>SQLException</code> is thrown.
     */
    public ArrayList<String> getRecordsList(String table, String key) {
        try {
            ArrayList<String> set = new ArrayList<>();

            String sql = "SELECT " + key + " FROM " + table;
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            while (resultSet.next()) {
                set.add(resultSet.getString(key));
            }
            return set;
        } catch (SQLException e) {
            App.logger.catching(e);
            return null;
        }
    }

}
