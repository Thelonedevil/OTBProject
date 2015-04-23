package com.github.otbproject.otbproject.api;

import com.github.otbproject.otbproject.commands.scheduler.ResetTask;
import com.github.otbproject.otbproject.commands.scheduler.ScheduledCommand;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Justin on 20/04/2015.
 */
public class APISchedule {

    public static void scheduleCommandInSeconds(String channel, String command, long delay,long period, boolean hourReset){
        Runnable task = new ScheduledCommand(channel,command);
        APIChannel.get(channel).getScheduledCommands().put(command, APIChannel.get(channel).getScheduler().schedule(task, delay, period, TimeUnit.SECONDS));
        if (hourReset){
            Runnable reset  = new ResetTask(channel,command,delay,period,TimeUnit.SECONDS);
            Date date = new Date();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            int delay1 = 60 - calendar.get(Calendar.MINUTE);
            APIChannel.get(channel).getHourlyResetSchedules().put(command,APIChannel.get(channel).getLongScheduler().schedule(reset,delay1,1,TimeUnit.HOURS));
        }
    }
    public static void scheduleCommandInMinutes(String channel, String command, long delay,long period, boolean hourReset){
        Runnable task = new ScheduledCommand(channel,command);
        APIChannel.get(channel).getScheduledCommands().put(command, APIChannel.get(channel).getScheduler().schedule(task, delay, period, TimeUnit.MINUTES));
        if (hourReset){
            Runnable reset  = new ResetTask(channel,command,delay,period,TimeUnit.MINUTES);
            Date date = new Date();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            int delay1 = 60 - calendar.get(Calendar.MINUTE);
            APIChannel.get(channel).getHourlyResetSchedules().put(command,APIChannel.get(channel).getLongScheduler().schedule(reset,delay1,1,TimeUnit.HOURS));
        }
    }
    public static void scheduleCommandInHours(String channel, String command, long delay,long period){
        Runnable task = new ScheduledCommand(channel,command);
        APIChannel.get(channel).getScheduledCommands().put(command, APIChannel.get(channel).getLongScheduler().schedule(task, delay, period, TimeUnit.HOURS));
    }

    public static void unScheduleCommand(String channel, String command){
        APIChannel.get(channel).getScheduledCommands().get(command).cancel(false);
        if (APIChannel.get(channel).getHourlyResetSchedules().containsKey(command)){
            APIChannel.get(channel).getHourlyResetSchedules().get(command).cancel(false);
        }
    }
}
