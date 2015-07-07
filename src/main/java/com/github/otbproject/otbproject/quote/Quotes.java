package com.github.otbproject.otbproject.quote;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.database.SQLiteQuoteWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Quotes {
    public static Quote get(SQLiteQuoteWrapper db, Integer id) {
        Optional<Quote> optional = db.getRecord(QuoteFields.TABLE_NAME, id, QuoteFields.ID, Quotes::getQuoteFromResultSet);
        return optional.orElse(null); // TODO return an optional and update references
    }

    public static Quote get(SQLiteQuoteWrapper db, String text) {
        Optional<Quote> optional = db.getRecord(QuoteFields.TABLE_NAME, text, QuoteFields.TEXT, Quotes::getQuoteFromResultSet);
        return optional.orElse(null); // TODO return an optional and update references
    }

    public static Quote getRandomQuote(SQLiteQuoteWrapper db) {
        Optional<Quote> optional = db.getRandomRecord(QuoteFields.TABLE_NAME, Quotes::getQuoteFromResultSet);
        return optional.orElse(null); // TODO return an optional and update references
    }

    private static Quote getQuoteFromResultSet(ResultSet rs) throws SQLException {
        if (rs.isAfterLast()) {
            return null;
        }
        Quote quote = new Quote();
        quote.setId(rs.getInt(QuoteFields.ID));
        quote.setText(rs.getString(QuoteFields.TEXT));
        return quote;
    }

    public static List<Integer> getQuoteIds(SQLiteQuoteWrapper db) {
        List<Object> list =  db.getNonRemovedRecordsList(QuoteFields.TABLE_NAME, QuoteFields.ID);
        if (list == null) {
            return null;
        }
        try {
            return list.stream().map(key -> Integer.valueOf((String) key)).collect(Collectors.toList());
        } catch (ClassCastException e) {
            App.logger.catching(e);
            return null;
        }
    }

    public static boolean update(SQLiteQuoteWrapper db, HashMap<String, Object> map) {
        return db.updateRecord(QuoteFields.TABLE_NAME, map.get(QuoteFields.ID), QuoteFields.ID, map);
    }

    // Tells you if the id is in the database, but not if it's associated with non-null quote text
    private static boolean exists(SQLiteQuoteWrapper db, Integer id) {
        return db.exists(QuoteFields.TABLE_NAME, id, QuoteFields.ID);
    }

    public static boolean exists(SQLiteQuoteWrapper db, String quoteText) {
        return db.exists(QuoteFields.TABLE_NAME, quoteText, QuoteFields.TEXT);
    }

    public static boolean existsAndNotRemoved(SQLiteQuoteWrapper db, Integer id) {
        return (get(db, id) != null);
    }

    public static boolean add(SQLiteQuoteWrapper db, HashMap<String, Object> map) {
        return db.insertRecord(QuoteFields.TABLE_NAME, map);
    }

    public static boolean remove(SQLiteQuoteWrapper db, Integer id) {
        return db.removeRecord(QuoteFields.TABLE_NAME, id, QuoteFields.ID);
    }

    public static boolean addQuoteFromObj(SQLiteQuoteWrapper db, Quote quote) {
        HashMap<String, Object> map = new HashMap<>();

        map.put(QuoteFields.TEXT, quote.getText());

        if (exists(db, quote.getId())) {
            return update(db, map);
        } else {
            return add(db, map);
        }
    }
}
