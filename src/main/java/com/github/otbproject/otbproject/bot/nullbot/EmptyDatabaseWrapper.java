package com.github.otbproject.otbproject.bot.nullbot;

import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.database.SQLFunction;

import java.util.*;

class EmptyDatabaseWrapper implements DatabaseWrapper {
    @Override
    public <R> Optional<R> getRecord(String table, List<Map.Entry<String, Object>> entryList, SQLFunction<R> function) {
        return Optional.empty();
    }

    @Override
    public <R> Optional<R> getRecord(String table, Object identifier, String fieldName, SQLFunction<R> function) {
        return Optional.empty();
    }

    @Override
    public <R> Optional<R> getRandomRecord(String table, SQLFunction<R> function) {
        return Optional.empty();
    }

    @Override
    public boolean exists(String table, List<Map.Entry<String, Object>> entryList) {
        return false;
    }

    @Override
    public boolean exists(String table, Object identifier, String fieldName) {
        return false;
    }

    @Override
    public boolean updateRecord(String table, Object identifier, String fieldName, HashMap<String, Object> map) {
        return false;
    }

    @Override
    public boolean insertRecord(String table, HashMap<String, Object> map) {
        return false;
    }

    @Override
    public boolean removeRecord(String table, List<Map.Entry<String, Object>> entryList) {
        return false;
    }

    @Override
    public boolean removeRecord(String table, Object identifier, String fieldName) {
        return false;
    }

    @Override
    public <R> Optional<R> tableDump(String table, SQLFunction<R> function) {
        return Optional.empty();
    }

    @Override
    public List<Object> getRecordsList(String table, String key) {
        return Collections.emptyList();
    }
}
