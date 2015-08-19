package com.github.otbproject.otbproject.database;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.quote.QuoteFields;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class SQLiteQuoteWrapper extends DatabaseWrapper {
    private final Lock lock = new ReentrantLock();

    private SQLiteQuoteWrapper(String path, HashMap<String, TableFields> tables) throws SQLException, ClassNotFoundException {
        super(path, tables);
    }

    public static SQLiteQuoteWrapper createDatabase(String path, HashMap<String, TableFields> tables) {
        try {
            return new SQLiteQuoteWrapper(path, tables);
        } catch (SQLException | ClassNotFoundException e) {
            App.logger.catching(e);
            return null;
        }
    }

    @Override
    public boolean removeRecord(String table, List<Map.Entry<String, Object>> entryList) {
        String sql = "UPDATE " + table + " SET " + QuoteFields.TEXT + "= NULL WHERE ";
        sql += entryList.stream().map(entry -> (entry.getKey() + "=?")).collect(Collectors.joining(", "));
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int index = 1;
            for (Map.Entry entry : entryList) {
                setValue(preparedStatement, index, entry.getValue());
                index++;
            }
            return (preparedStatement.executeUpdate() > 0);
        } catch (SQLException e) {
            App.logger.error("SQL: " + sql);
            App.logger.catching(e);
            return false;
        }
    }

    @Override
    public boolean insertRecord(String table, HashMap<String, Object> map) {
        // Try to fill empty slot
        String sql = "SELECT " + QuoteFields.ID + " FROM " + table + " WHERE " + QuoteFields.TEXT + " IS NULL ORDER BY " + QuoteFields.ID + " ASC LIMIT 1";
        lock.lock();
        try (ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Integer id = rs.getInt(QuoteFields.ID);
                map.put(QuoteFields.ID, id);
                return updateRecord(table, id, QuoteFields.ID, map);
            }
        } catch (SQLException e) {
            App.logger.error("SQL: " + sql);
            App.logger.catching(e);
            return false;
        } finally {
            lock.unlock();
        }

        // Create new record otherwise
        return super.insertRecord(table, map);
    }

    @Override
    public <R> Optional<R> getRandomRecord(String table, SQLFunction<R> function) {
        String sql = "SELECT * FROM " + table + " WHERE " + QuoteFields.TEXT + " IS NOT NULL ORDER BY RANDOM() LIMIT 1";
        try (ResultSet rs = stmt.executeQuery(sql)) {
            return Optional.ofNullable(function.apply(rs));
        } catch (SQLException e) {
            App.logger.error("SQL: " + sql);
            App.logger.catching(e);
            return Optional.empty();
        }
    }

    public ArrayList<Object> getNonRemovedRecordsList(String table, String key) {
        String sql = "SELECT " + key + " FROM " + table + " WHERE " + QuoteFields.TEXT + " IS NOT NULL";
        try (ResultSet rs = stmt.executeQuery(sql)) {
            ArrayList<Object> set = new ArrayList<>();
            while (rs.next()) {
                set.add(rs.getString(key));
            }
            return set;
        } catch (SQLException e) {
            App.logger.error("SQL: " + sql);
            App.logger.catching(e);
            return null;
        }
    }
}
