package com.github.otbproject.otbproject.database;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLFunction<R> {
    R apply(ResultSet rs) throws SQLException;
}
