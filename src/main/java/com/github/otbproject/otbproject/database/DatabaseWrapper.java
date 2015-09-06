package com.github.otbproject.otbproject.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DatabaseWrapper {
    <R> Optional<R> getRecord(String table, List<Map.Entry<String, Object>> entryList, SQLFunction<R> function);

    <R> Optional<R> getRecord(String table, Object identifier, String fieldName, SQLFunction<R> function);

    <R> Optional<R> getRandomRecord(String table, SQLFunction<R> function);

    boolean exists(String table, List<Map.Entry<String, Object>> entryList);

    boolean exists(String table, Object identifier, String fieldName);

    boolean updateRecord(String table, Object identifier, String fieldName, HashMap<String, Object> map);

    boolean insertRecord(String table, HashMap<String, Object> map);

    boolean removeRecord(String table, List<Map.Entry<String, Object>> entryList);

    boolean removeRecord(String table, Object identifier, String fieldName);

    <R> Optional<R> tableDump(String table, SQLFunction<R> function);

    List<Object> getRecordsList(String table, String key);
}
