package com.github.otbproject.otbproject.database;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by justin on 23/12/2014.
 */
public class DatabaseWrapper {
    Connection connect;
    Statement statement;

    /**
     *
     * @param path path to the database
     * @param tableMap a HashMap of table names and HashMaps of table fields
     *                 and field types for each table.
     */
    public DatabaseWrapper(String path, HashMap<String, HashMap<String, String>> tableMap) {
        try {
            File f = new File(path);
            if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
            connect = connect(path);
            statement = state(connect);

            for (String tableName : tableMap.keySet()) {
                createTable(tableName, tableMap.get(tableName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param path , path to the .sqlite file
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws NullPointerException
     */
    Connection connect(String path) throws SQLException, ClassNotFoundException, NullPointerException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        return connection;
    }

    /**
     * @param connection , the connection to a database, {@link #connect(String)}
     * @param timeout    , the timeout for the query
     *                   {@link Statement#setQueryTimeout(int)}
     * @return Returns a {@link Statement}
     * @throws SQLException
     */
    public Statement state(Connection connection, int timeout) {
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(timeout); // set timeout to 30 sec.
            return statement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param connection , the connection to a database, {@link #connect(String)}
     * @return Returns a {@link Statement}
     * @throws SQLException
     */
    public Statement state(Connection connection) {
        return state(connection, 30); // set timeout to 30 sec.

    }

    /**
     * @param table_name
     * @param fields
     * @throws SQLException
     */
    void createTable(String table_name, HashMap<String, String> fields) throws SQLException {
        String query = "create table if not exists " + table_name + " (";
        for (String key : fields.keySet()) {
            query = query + key + " " + fields.get(key) + ", ";
        }
        query = query.substring(0, query.length() - 2);
        query = query + ")";
        statement.executeUpdate(query);
    }

    /**
     * @param query
     * @return
     * @throws SQLException
     */
    public ResultSet rs(String query) throws SQLException {
        ResultSet rs = statement.executeQuery(query);
        return rs;
    }

    /**
     * @param table
     * @param identifier
     * @param fieldName
     * @return A HashMap<Column_label,Value> for the row_label if the row
     * exists, otherwise returns an empty HashMap<String,Object>
     * @throws SQLException
     */
    public HashMap<String, Object> getRow(String table, String identifier, String fieldName) throws SQLException {
        HashMap<String, Object> row = new HashMap<String, Object>();
        String query = "SELECT * FROM " + table;
        ResultSet rs1 = rs(query);
        while (rs1.next()) {
            if (rs1.getString(fieldName).equalsIgnoreCase(identifier)) {
                ResultSetMetaData rsmd = rs1.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                int index = 1;
                while (index <= columnsNumber) {
                    String Column_Label = rsmd.getColumnName(index);
                    row.put(Column_Label, rs1.getObject(index));
                    index++;
                }
            }
        }
        rs1.close();
        return row;
    }

    public Object getValue(String table, String identifier, String fieldName, String fieldToGet) throws SQLException {
        String query = "SELECT * FROM " + table;
        ResultSet rs1 = rs(query);
        while (rs1.next()) {
            if (rs1.getString(fieldName).equalsIgnoreCase(identifier)) {
                ResultSetMetaData rsmd = rs1.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                int index = 1;
                while (index <= columnsNumber) {
                    String Column_Label = rsmd.getColumnName(index);
                    if (Column_Label.equalsIgnoreCase(fieldToGet)) {
                        return rs1.getObject(index);
                    }
                    index++;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param table
     * @param identifier
     * @param fieldName
     * @return
     * @throws SQLException
     */
    public boolean exists(String table, String identifier, String fieldName) throws SQLException {
        String query = "SELECT " + fieldName + " FROM " + table;
        ResultSet rs1 = rs(query);
        while (rs1.next()) {
            if (rs1.getString(fieldName).equalsIgnoreCase(identifier)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param table
     * @param identifier
     * @param fieldName
     * @param map
     * @throws SQLException
     */
    public void updateRow(String table, String identifier, String fieldName, HashMap<String, Object> map) throws SQLException {
        String update = "UPDATE " + table + " SET  ";
        for (String key : map.keySet()) {
            update = update + key + "='" + map.get(key) + "', ";
        }
        update = update.substring(0,update.length()-2);
        update = update + " WHERE " + fieldName + "='" + identifier + "';";
        statement.executeUpdate(update);
    }

    /**
     * @param table
     * @param identifier
     * @param fieldName
     * @param map
     * @throws SQLException
     */
    public void insertRow(String table, String identifier, String fieldName, HashMap<String, Object> map) throws SQLException {
        String first = "INSERT INTO " + table + " (" + fieldName;
        String last = "VALUES ('" + identifier + "'";
        for (String key : map.keySet()) {
            first = first + (", " + key);
            last = last + (", '" + map.get(key)+"'");
        }
        first = first + ") ";
        last = last + ")";
        String update = first + last;
        statement.executeUpdate(update);

    }

    public void removeRow(String table, String identifier, String fieldName) throws SQLException {
        String update = "DELETE FROM "+ table + " WHERE "+ fieldName+"="+"'"+identifier+"'";
        statement.executeUpdate(update);
    }

    public HashMap<String,HashMap<String,Object>> getRecords(String table, String key) throws SQLException {
        HashMap<String,HashMap<String,Object>> map = new HashMap<>();
        String query = "SELECT * FROM " + table;
        ResultSet rs1 = rs(query);
        while (rs1.next()) {

            int columns = rs1.getMetaData().getColumnCount();
            HashMap<String,Object> map1 = new HashMap<>();
            // Yes i know normally you start loops at 0 but the column indices start at 1
            for (int i = 1; i == columns ; i++) {
                String columnName = rs1.getMetaData().getColumnLabel(i);
                Object data = rs1.getObject(i);
                map1.put(columnName,data);
            }
            map.put((String)map1.get(key),map1);
        }
        return map;
    }
    public ArrayList<String> getRecordsList(String table, String key) throws SQLException {
        ArrayList<String> list = new ArrayList<>();
        String query = "SELECT * FROM " + table;
        ResultSet rs1 = rs(query);
        while (rs1.next()) {
            list.add((String)rs1.getObject(key));
        }
        return list;
    }
}
