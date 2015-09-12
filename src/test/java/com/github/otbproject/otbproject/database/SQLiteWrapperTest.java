package com.github.otbproject.otbproject.database;

import com.github.otbproject.otbproject.fs.FSUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

public class SQLiteWrapperTest {
    public static final String path = "target/test.db";
    public static final HashMap<String, TableFields> tables = new HashMap<>();
    public static final String tableName = "tblTest";
    public static TableFields testFields;
    public static final HashMap<String, String> fields = new HashMap<>();
    public static DatabaseWrapper db;
    public static final HashMap<String, Object> testData = new HashMap<>();
    public static final HashMap<String, Object> testDataNew = new HashMap<>();
    public static final HashMap<String, Object> testDataInt = new HashMap<>();
    public static final String fieldName = "testField";
    public static final String fieldName2 = "testIntField";


    @BeforeClass
    public static void initialise() {
        System.setProperty("OTBCONF", "./logs/");
        System.setProperty("OTBDEBUG","false");
        fields.put(fieldName, DataTypes.STRING);
        fields.put(fieldName2, DataTypes.INTEGER);
        HashSet<String> primaryKeys = new HashSet<>();
        primaryKeys.add(fieldName);
        testFields = new TableFields(fields, primaryKeys);
        tables.put(tableName, testFields);
        db = SQLiteWrapper.createDatabase(path, tables);
        testData.put(fieldName, "Test Data");
        testDataNew.put(fieldName, "Test Data New");
        testDataInt.put(fieldName2, 1);

    }
    @AfterClass
    public static void clean(){
        new File(path).delete();
    }

    @Test
    public void tableDataShouldBeDumped(){
        assertTrue(db.tableDump(tableName, rs -> (rs == null) ? null : 0).isPresent());
    }
    @Test
    public void tableDataShouldNotBeDumped(){
        assertFalse(db.tableDump(tableName + "poop", rs -> (rs == null) ? null : 0).isPresent());
    }
    @Test
    public void listOfRecordsShouldBeRetrieved(){
        assertNotNull(db.getRecordsList(tableName, fieldName));
    }
    @Test
    public void listOfRecordsShouldNotBeRetrieved(){
        assertNull(db.getRecordsList(tableName+"poop",fieldName));
    }
    @Test
    public void stringDataShouldBeInsertedIntoAndRemovedFromDatabase() {
        assertFalse(db.exists(tableName, testData.get(fieldName), fieldName));
        assertTrue(db.insertRecord(tableName, testData));
        assertTrue(db.exists(tableName, testData.get(fieldName), fieldName));
        assertTrue(db.removeRecord(tableName, testData.get(fieldName), fieldName));
        assertFalse(db.exists(tableName, testData.get(fieldName), fieldName));
    }

    @Test
    public void integerDataShouldBeInsertedIntoAndRemovedFromDatabase(){
        assertFalse(db.exists(tableName, testDataInt.get(fieldName2), fieldName2));
        assertTrue(db.insertRecord(tableName, testDataInt));
        assertTrue(db.exists(tableName, testDataInt.get(fieldName2), fieldName2));
        assertTrue(db.removeRecord(tableName, testDataInt.get(fieldName2), fieldName2));
        assertFalse(db.exists(tableName, testDataInt.get(fieldName2), fieldName2));
    }

    @Test
    public void dataShouldNotBeInsertedIntoDatabase(){
        assertFalse(db.insertRecord("poop", testData));
    }
    @Test
    public void dataShouldNotBeRemovedFromDatabase(){
        assertFalse(db.removeRecord(tableName, testData.get(fieldName), fieldName));
    }

    @Test
    public void newDataShouldReplaceOldDataInDatabase(){
        assertFalse(db.exists(tableName, testData.get(fieldName), fieldName));
        assertTrue(db.insertRecord(tableName, testData));
        assertTrue(db.exists(tableName, testData.get(fieldName), fieldName));
        assertTrue(db.updateRecord(tableName, testData.get(fieldName), fieldName, testDataNew));
        assertFalse(db.exists(tableName, testData.get(fieldName), fieldName));
        assertTrue(db.exists(tableName, testDataNew.get(fieldName), fieldName));
        assertTrue(db.removeRecord(tableName, testDataNew.get(fieldName), fieldName));
    }

    @Test
    public void dataShouldBeInsertedIntoAndRetrievedFromDatabase() throws SQLException {
        assertTrue(db.insertRecord(tableName, testData));
        assertTrue(db.exists(tableName, testData.get(fieldName), fieldName));
        db.getRecord(tableName, testData.get(fieldName), fieldName,
                rs -> {
                    assertEquals(testData.get(fieldName), rs.getString(fieldName));
                    return null;
                });
        assertTrue(db.removeRecord(tableName, testData.get(fieldName), fieldName));
    }
}
