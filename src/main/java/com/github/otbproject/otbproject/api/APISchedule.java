package com.github.otbproject.otbproject.api;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.commands.scheduler.ResetTask;
import com.github.otbproject.otbproject.commands.scheduler.ScheduledCommand;
import com.github.otbproject.otbproject.commands.scheduler.SchedulerFields;
import com.github.otbproject.otbproject.database.DatabaseWrapper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Justin on 20/04/2015.
 */
public class APISchedule {

    public static void scheduleCommandInSeconds(String channel, String command, long delay, long period, boolean hourReset) {
        scheduleCommand(channel, command, delay, period, hourReset, TimeUnit.SECONDS);
    }

    public static void scheduleCommandInMinutes(String channel, String command, long delay, long period, boolean hourReset) {
        scheduleCommand(channel, command, delay, period, hourReset, TimeUnit.MINUTES);
    }

    public static void scheduleCommandInHours(String channel, String command, long delay, long period) {
        scheduleCommand(channel, command, delay, period, false, TimeUnit.HOURS);
    }

    public static void unScheduleCommand(String channel, String command) {
        APIChannel.get(channel).getScheduledCommands().get(command).cancel(false);
        if (APIChannel.get(channel).getHourlyResetSchedules().containsKey(command))
            APIChannel.get(channel).getHourlyResetSchedules().get(command).cancel(false);
    }

    public static int getMinutesTillTheHour() {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return 60 - calendar.get(Calendar.MINUTE);
    }

    private static boolean scheduleCommand(String channel, String command, long delay, long period, boolean hourReset, TimeUnit timeUnit) {
        Runnable task = new ScheduledCommand(channel, command);
        APIChannel.get(channel).getScheduledCommands().put(command, APIChannel.get(channel).getScheduler().schedule(task, getMinutesTillTheHour() + delay, period, timeUnit));
        App.logger.debug("Scheduled Command: " + command + " to run every " + period + " " + timeUnit.toString());
        if (hourReset) {
            Runnable reset = new ResetTask(channel, command, delay, period, timeUnit);
            APIChannel.get(channel).getHourlyResetSchedules().put(command, APIChannel.get(channel).getScheduler().schedule(reset, getMinutesTillTheHour(), 1, TimeUnit.HOURS));
        }
        return addToDatabase(channel, command, delay, period, hourReset, timeUnit);
    }

    private static boolean addToDatabase(String channel, String command, long delay, long period, boolean hourReset, TimeUnit timeUnit) {
        DatabaseWrapper db = APIChannel.get(channel).getMainDatabaseWrapper();
        if (db.exists(SchedulerFields.TABLE_NAME, command, SchedulerFields.COMMAND)) {
            return false;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put(SchedulerFields.COMMAND, command);
        map.put(SchedulerFields.TYPE, timeUnit.toString());
        map.put(SchedulerFields.OFFSET, delay);
        map.put(SchedulerFields.PERIOD, period);
        map.put(SchedulerFields.RESET, Boolean.toString(hourReset));
        return db.insertRecord(SchedulerFields.TABLE_NAME, map);
    }
}
