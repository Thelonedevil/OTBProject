package com.github.otbproject.otbproject.quotes;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.database.DatabaseWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Quotes {
    public static Quote get(DatabaseWrapper db, Integer id) {
        Quote quote = null;
        if (db.exists(QuoteFields.TABLE_NAME, id, QuoteFields.ID)) {
            ResultSet rs = db.getRecord(QuoteFields.TABLE_NAME, id, QuoteFields.ID);
            quote = getQuoteFromResultSet(rs);
        }
        return quote;
    }

    public static Quote getRandomQuote(DatabaseWrapper db) {
        ResultSet rs = db.getRandomRecord(QuoteFields.TABLE_NAME);
        return getQuoteFromResultSet(rs);
    }

    private static Quote getQuoteFromResultSet(ResultSet rs) {
        Quote quote = new Quote();
        try {
            quote.setId(rs.getInt(QuoteFields.ID));
            quote.setText(rs.getString(QuoteFields.TEXT));
        } catch (SQLException e) {
            App.logger.catching(e);
            return null;
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                App.logger.catching(e);
            }
        }
        return quote;
    }

    public static ArrayList<Integer> getQuoteIds(DatabaseWrapper db) {
        ArrayList<Object> objectArrayList =  db.getRecordsList(QuoteFields.TABLE_NAME, QuoteFields.ID);
        if (objectArrayList == null) {
            return null;
        }
        ArrayList<Integer> idsList = new ArrayList<>();
        try {
            idsList.addAll(objectArrayList.stream().map(key -> (Integer) key).collect(Collectors.toList()));
            return idsList;
        } catch (ClassCastException e) {
            App.logger.catching(e);
            return null;
        }
    }

    public static boolean update(DatabaseWrapper db, HashMap<String, Object> map) {
        return db.updateRecord(QuoteFields.TABLE_NAME, map.get(QuoteFields.ID), QuoteFields.ID, map);
    }

    public static boolean exists(DatabaseWrapper db, Integer id) {
        return db.exists(QuoteFields.TABLE_NAME, id, QuoteFields.ID);
    }

    public static boolean add(DatabaseWrapper db, HashMap<String, Object> map) {
        return db.insertRecord(QuoteFields.TABLE_NAME, map);
    }

    public static boolean remove(DatabaseWrapper db, Integer id) {
        return db.removeRecord(QuoteFields.TABLE_NAME, id, QuoteFields.ID);
    }

    public static boolean addUserFromObj(DatabaseWrapper db, Quote quote) {
        HashMap<String, Object> map = new HashMap<>();

        map.put(QuoteFields.ID, quote.getId());
        map.put(QuoteFields.TEXT, quote.getText());

        if (exists(db, quote.getId())) {
            return update(db, map);
        } else {
            return add(db, map);
        }
    }
}
