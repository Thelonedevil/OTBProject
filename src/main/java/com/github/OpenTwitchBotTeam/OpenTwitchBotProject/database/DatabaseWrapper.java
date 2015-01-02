package com.github.OpenTwitchBotTeam.OpenTwitchBotProject.database;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

/**
 * Created by justin on 23/12/2014.
 */
public class DatabaseWrapper {
    Connection connect;
    Statement statement;

    /**
     *
     * @param path
     */
    public DatabaseWrapper(String path) {
        try {
            File f = new File(path);
            if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
            connect = connect(path);
            statement = state(connect);
            HashMap<String, String> commandFields = new HashMap<String, String>();
            commandFields.put(FieldNames.NAME, "Text");
            commandFields.put(FieldNames.EXECUSERLEVEL, "Text");
            commandFields.put(FieldNames.NAMEMODIFYINGUL, "Text");
            commandFields.put(FieldNames.RESPONSEMODIFYINGUL, "Text");
            commandFields.put(FieldNames.USERLEVELMODYFINGUL, "Text");
            commandFields.put(FieldNames.RESPONSE, "Text");
            commandFields.put(FieldNames.COUNT, "INTEGER");
            commandFields.put(FieldNames.ENABLED, "INTEGER");
            commandFields.put(FieldNames.SCRIPT, "TEXT");
            commandFields.put(FieldNames.MINARGS, "INTEGER");
            createTable(TableNames.COMMANDS, commandFields);

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
            update = update + ", " + key + "=" + map.get(key);
        }
        update = update + " WHERE " + fieldName + "=" + identifier + " ";
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
            last = last + (", " + map.get(key));
        }
        first = first + ") ";
        last = last + ")";
        String update = first + last;
        statement.executeUpdate(update);

    }

    public void removeRow(String table, String identifier, String fieldName) throws SQLException {
        String update = "DELETE FROM "+ table + " WHERE "+ fieldName+"="+identifier;
        statement.executeUpdate(update);
    }

    // TODO remove past this
    /**
     *
     * @param command
     * @return
     * @throws SQLException
     */
    public HashMap<String, Object> getCommandDetails(String command) throws SQLException {
        return this.getRow(TableNames.COMMANDS, command, FieldNames.NAME);
    }

    /**
     *
     * @param command
     * @return
     * @throws SQLException
     */
    public boolean isCommand(String command) throws SQLException {
        return this.exists(TableNames.COMMANDS, command, FieldNames.NAME);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public void updateCommand(HashMap map) throws SQLException {
        this.updateRow(TableNames.COMMANDS, (String) map.get(FieldNames.NAME), FieldNames.NAME, map);
    }

    public void updateComamnd(String command,String fieldName, String fieldValue) throws SQLException {
        HashMap<String,Object> map = this.getCommandDetails(command);
        map.replace(fieldName,fieldValue);
        updateCommand(map);
    }

    /**
     *
     * @param map
     * @throws SQLException
     */
    public void addCommand(HashMap map) throws SQLException {
        this.insertRow(TableNames.COMMANDS, (String) map.get(FieldNames.NAME), FieldNames.NAME, map);
    }

    public void removeCommand(String command) throws SQLException {
        this.removeRow(TableNames.COMMANDS,command,FieldNames.NAME);
    }

}
