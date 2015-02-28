package com.github.otbproject.otbproject.database;

import com.github.otbproject.otbproject.App;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by justin on 23/12/2014.
 */
public class DatabaseWrapper extends Object {
    final Connection connection;

    private DatabaseWrapper(String path, HashMap<String, HashSet<String>> tables) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        for (String key : tables.keySet()) {
            createTable(key, tables.get(key));
        }
    }

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

    public boolean createTable(String name, HashSet<String> table) {
        return createTable(name, table, null);
    }

    public boolean createTable(String name, HashSet<String> table, String primaryKey) {
        PreparedStatement preparedStatement = null;
        String sql = "CREATE TABLE IF NOT EXISTS " + name + " (";
        for (String key : table) {
            if (key.equals(primaryKey)) {
                sql = sql + key +" PRIMARY KEY TEXT, ";
            } else {
                sql = sql + key +" TEXT, ";
            }
        }
        sql = sql.substring(0, sql.length() - 2) + ")";
        boolean bool = false;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            bool = preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.logger.catching(e);
            }

        }
        return bool;
    }

    public ResultSet getRecord(String table, String identifier, String fieldName) {
        PreparedStatement preparedStatement = null;
        String sql = "SELECT " + fieldName + " FROM " + table + " WHERE " + fieldName + "= ?";
        ResultSet rs = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, identifier);
            rs = preparedStatement.getResultSet();
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.logger.catching(e);
            }

        }
        return rs;
    }

    public boolean exists(String table, String identifier, String fieldName) {
        PreparedStatement preparedStatement = null;
        String sql = "SELECT " + fieldName + " FROM " + table + " WHERE " + fieldName + "= ?";
        boolean bool = false;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, identifier);
            bool = preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.logger.catching(e);
            }

        }
        return bool;
    }

    public boolean updateRecord(String table, String identifier, String fieldName, HashMap<String, String> map) {
        PreparedStatement preparedStatement = null;
        String sql = "UPDATE " + table + " SET ";
        for (String key : map.keySet()) {
            sql += key+"=?, ";
        }
        sql = sql.substring(0, sql.length() - 2);
        sql += "WHERE " + fieldName + "= ?";
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
            bool = preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.logger.catching(e);
            }

        }
        return bool;
    }

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
            preparedStatement.setString(index, identifier);
            bool = preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.logger.catching(e);
            }

        }
        return bool;
    }

    public boolean removeRecord(String table, String identifier, String fieldName) {
        PreparedStatement preparedStatement = null;
        String sql = "DELETE FROM " + table + " WHERE " + fieldName + "=?";
        boolean bool = false;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, identifier);
            bool = preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            App.logger.catching(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.logger.catching(e);
            }

        }
        return bool;
    }

    public ResultSet tableDump(String table) {
        String sql = "SELECT * FROM " + table;
        try {
            return connection.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            return null;
        }

    }

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
            return null;
        }
    }

}
