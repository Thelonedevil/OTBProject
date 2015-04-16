package com.github.otbproject.otbproject.database;

import java.util.HashMap;

public class TableFields {
    public final HashMap<String, String> map;
    public final String primaryKey;

    public TableFields(HashMap<String, String> map, String primaryKey) {
        this.map = map;
        this.primaryKey = primaryKey;
    }
}
