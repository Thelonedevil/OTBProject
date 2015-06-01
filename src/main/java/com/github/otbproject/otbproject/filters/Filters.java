package com.github.otbproject.otbproject.filters;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.util.CustomCollectors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Filters {
    // TODO fix to take 2 params
    public static BasicFilter get(DatabaseWrapper db, String data, FilterType type) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(FilterFields.DATA, data);
        map.put(FilterFields.TYPE, type.name());
        // TODO fix
        if (db.exists(FilterFields.TABLE_NAME, data, FilterFields.DATA)) {
            ResultSet rs = db.getRecord(FilterFields.TABLE_NAME, map);
            try {
                return getFilterFromResultSet(rs);
            } catch (SQLException e) {
                App.logger.catching(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    App.logger.catching(e);
                }
            }
        }
        return null;
    }

    public static ArrayList<BasicFilter> getBasicFilters(DatabaseWrapper db) {
        ArrayList<BasicFilter> filters = new ArrayList<>();
        ResultSet rs = db.tableDump(FilterFields.TABLE_NAME);
        try {
            while (rs.next()) {
                filters.add(getFilterFromResultSet(rs));
            }
        } catch (SQLException e) {
            App.logger.catching(e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                App.logger.catching(e);
            }
        }
        return filters;
    }

    public static ConcurrentHashMap.KeySetView<Filter, Boolean> getAllFilters(DatabaseWrapper db) {
        return getBasicFilters(db).stream().map(Filter::fromBasicFilter).collect(CustomCollectors.toConcurrentSet());
    }

    // TODO possibly remove
    public static List<String> getFilterDataOfType(DatabaseWrapper db, FilterType type) {
        return getBasicFilters(db).stream().filter(basicFilter -> (basicFilter.getType() == type)).map(BasicFilter::getData).collect(Collectors.toList());
    }

    private static BasicFilter getFilterFromResultSet(ResultSet rs) throws SQLException {
        BasicFilter basicFilter = new BasicFilter();
        basicFilter.setData(rs.getString(FilterFields.DATA));
        basicFilter.setType(FilterType.valueOf(rs.getString(FilterFields.TYPE)));
        basicFilter.setGroup(rs.getString(FilterFields.GROUP));
        basicFilter.setEnabled(Boolean.valueOf(rs.getString(FilterFields.ENABLED)));
        return basicFilter;
    }

    public static boolean update(DatabaseWrapper db, HashMap<String, Object> map) {
        return db.updateRecord(FilterFields.TABLE_NAME, map.get(FilterFields.DATA), FilterFields.DATA, map);
    }

    public static boolean exists(DatabaseWrapper db, String data) {
        return db.exists(FilterFields.TABLE_NAME, data, FilterFields.DATA);
    }

    public static boolean add(DatabaseWrapper db, HashMap<String, Object> map) {
        return db.insertRecord(FilterFields.TABLE_NAME, map);
    }

    public static boolean remove(DatabaseWrapper db, String data) {
        return db.removeRecord(FilterFields.TABLE_NAME, data, FilterFields.DATA);
    }

    public static boolean addFilterFromObj(DatabaseWrapper db, BasicFilter basicFilter) {
        HashMap<String, Object> map = new HashMap<>();

        map.put(FilterFields.DATA, basicFilter.getData());
        map.put(FilterFields.TYPE, basicFilter.getType().name());
        map.put(FilterFields.GROUP, basicFilter.getGroup());
        map.put(FilterFields.ENABLED, basicFilter.isEnabled().toString());

        if (exists(db, basicFilter.getData())) {
            return update(db, map);
        } else {
            return add(db, map);
        }
    }
}
