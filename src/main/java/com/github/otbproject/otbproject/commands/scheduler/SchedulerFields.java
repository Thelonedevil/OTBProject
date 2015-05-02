package com.github.otbproject.otbproject.commands.scheduler;

import com.github.otbproject.otbproject.database.DataTypes;
import com.github.otbproject.otbproject.database.TableFields;

import java.util.HashMap;

/**
 * Created by Justin on 20/04/2015.
 */
public class SchedulerFields {
    public static final String COMMAND = "command";
    public static final String TYPE = "type";
    public static final String OFFSET = "offset";
    public static final String PERIOD = "period";
    public static final String RESET = "reset";

    public static final String TABLE_NAME = "tblSchedule";
    public static final String PRIMARY_KEY = COMMAND;

    public static TableFields getTableFields() {
        HashMap<String, String> fields = new HashMap<>();
        fields.put(COMMAND, DataTypes.STRING);
        fields.put(TYPE,DataTypes.STRING);
        fields.put(OFFSET,DataTypes.INTEGER);
        fields.put(PERIOD,DataTypes.INTEGER);
        fields.put(RESET,DataTypes.STRING);
        return new TableFields(fields, PRIMARY_KEY);
    }
}
