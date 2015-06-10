package com.github.otbproject.otbproject.commands.scheduler;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.channels.Channels;
import com.github.otbproject.otbproject.database.DatabaseWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Schedules {
    public static void scheduleCommandInSeconds(String channel, String command, long delay, long period, boolean hourReset) {
        scheduleCommand(channel, command, delay, period, hourReset, TimeUnit.SECONDS);
    }

    public static void scheduleCommandInMinutes(String channel, String command, long delay, long period, boolean hourReset) {
        scheduleCommand(channel, command, (TimeUnit.MINUTES.toSeconds(delay)), (TimeUnit.MINUTES.toSeconds(period)), hourReset, TimeUnit.SECONDS);
    }

    public static void scheduleCommandInHours(String channel, String command, long delay, long period) {
        scheduleCommand(channel, command, delay, period, false, TimeUnit.HOURS);
    }

    public static boolean isScheduled(String channel, String command){
        return Channels.get(channel).getScheduledCommands().containsKey(command);
    }

    public static void unScheduleCommand(String channel, String command) {
        Channels.get(channel).getScheduledCommands().get(command).cancel(false);
        if (Channels.get(channel).getHourlyResetSchedules().containsKey(command))
            Channels.get(channel).getHourlyResetSchedules().get(command).cancel(false);
    }

    public static long getSecondsSinceTheHour() {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return TimeUnit.MINUTES.toSeconds(calendar.get(Calendar.MINUTE)) + calendar.get(Calendar.SECOND);
    }

    public static long getSecondsTillTheHour() {
        return TimeUnit.HOURS.toSeconds(1) - getSecondsSinceTheHour();
    }

    private static boolean scheduleCommand(String channelName, String command, long delay, long period, boolean hourReset, TimeUnit timeUnit) {
        Channel channel = Channels.get(channelName);
        if (channel == null) {
            App.logger.error("Cannot schedule command for channel '" + channelName + "' - channel is null.");
            return false;
        }
        doScheduleCommand(channel, command, delay, period, hourReset, timeUnit);
        return addToDatabase(channelName, command, delay, period, hourReset, timeUnit);
    }

    // Channel should not be null
    private static void doScheduleCommand(Channel channel, String command, long delay, long period, boolean hourReset, TimeUnit timeUnit) {
        Runnable task = new ScheduledCommand(channel.getName(), command);
        if (!hourReset) {
            scheduleTask(channel, command, task, delay, period, timeUnit);
        } else {
            long result = getSecondsSinceTheHour() - delay;
            if (result < 0) {
                scheduleTask(channel, command, task, (-1 * result), period, timeUnit);
            } else {
                // This gets the correct delay. Trust me
                scheduleTask(channel, command, task, (period - (result % period)), period, timeUnit);
            }
            // Setup reset
            Runnable reset = new ResetTask(channel.getName(), command, delay, period, timeUnit);
            channel.getHourlyResetSchedules().put(command, channel.getScheduler().schedule(reset, getSecondsTillTheHour(), TimeUnit.HOURS.toSeconds(1), TimeUnit.SECONDS));
        }
    }

    // Channel should not be null
    private static boolean scheduleTask(Channel channel, String command, Runnable task, long delay, long period, TimeUnit timeUnit) {
        channel.getScheduledCommands().put(command, channel.getScheduler().schedule(task, delay, period, timeUnit));
        App.logger.debug("Scheduled Command: " + command + " to run every " + period + " " + timeUnit.toString());
        return true;
    }

    private static boolean addToDatabase(String channel, String command, long delay, long period, boolean hourReset, TimeUnit timeUnit) {
        DatabaseWrapper db = Channels.get(channel).getMainDatabaseWrapper();
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

    public static void loadFromDatabase(String channelName) {
        Channel channel = Channels.get(channelName);
        if (channel == null) {
            return;
        }
        loadFromDatabase(channel);
    }
    public static void loadFromDatabase(Channel channel) {
        DatabaseWrapper db = channel.getMainDatabaseWrapper();
        ResultSet rs = db.tableDump(SchedulerFields.TABLE_NAME);
        try {
            while (rs.next()) {
                String command = rs.getString(SchedulerFields.COMMAND);
                long delay = rs.getLong(SchedulerFields.OFFSET);
                long period = rs.getLong(SchedulerFields.PERIOD);
                boolean hourReset = Boolean.parseBoolean(rs.getString(SchedulerFields.RESET));
                TimeUnit timeUnit =  TimeUnit.valueOf(rs.getString(SchedulerFields.TYPE));
                doScheduleCommand(channel, command, delay, period, hourReset, timeUnit);
            }
        } catch (SQLException e) {
            App.logger.catching(e);
        }finally {
            try {
                rs.close();
            } catch (SQLException e) {
                App.logger.catching(e);
            }
        }

    }

    public static void removeFromDatabase(String channel, String command){
        DatabaseWrapper db = Channels.get(channel).getMainDatabaseWrapper();
        db.removeRecord(SchedulerFields.TABLE_NAME,command, SchedulerFields.COMMAND);
    }

}
