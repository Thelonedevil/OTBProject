package com.github.otbproject.otbproject.database;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DatabaseWrapperTest {
    public static final String path = "target/test.db";
    public static final HashMap<String, TableFields> tables = new HashMap<>();
    public static final String tableName = "tblTest";
    public static TableFields testFields;
    public static final HashMap<String, String> fields = new HashMap<>();
    public static DatabaseWrapper db;
    public static final HashMap<String, Object> testData = new HashMap<>();
    public static final String fieldName = "testField";


    @BeforeClass
    public static void initialise() {
        System.setProperty("OTBCONF", "target/logs");
        fields.put(fieldName, DataTypes.STRING);
        testFields = new TableFields(fields, fieldName);
        tables.put(tableName, testFields);
        db = DatabaseWrapper.createDatabase(path, tables);
        testData.put(fieldName, "Test Data");

    }
    @AfterClass
    public static void clean(){
        new File(path).delete();
    }


    @Test
    public void dataShouldBeInsertedIntoAndRemovedFromDatabase() {
        assertTrue(db.insertRecord(tableName, testData));
        assertTrue(db.exists(tableName, testData.get(fieldName), fieldName));
        assertTrue(db.removeRecord(tableName, testData.get(fieldName), fieldName));
    }
    @Test
    public void dataShouldBeInsertedIntoAndRetrievedFromDatabase() {
        assertTrue(db.insertRecord(tableName, testData));
        assertTrue(db.exists(tableName, testData.get(fieldName), fieldName));
        ResultSet rs = db.getRecord(tableName, testData.get(fieldName), fieldName);
        try {
            while(rs.next()){
                String output = rs.getString(fieldName);
                assertEquals(output,testData.get(fieldName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
