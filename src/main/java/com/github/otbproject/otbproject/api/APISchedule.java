package com.github.otbproject.otbproject.api;

import com.github.otbproject.otbproject.commands.scheduler.ScheduledCommand;

import java.util.concurrent.TimeUnit;

/**
 * Created by Justin on 20/04/2015.
 */
public class APISchedule {

    public static void scheduleCommandInMilliSeconds(String channel, String command, long delay,long period){
        Runnable task = new ScheduledCommand(channel,command);
        APIChannel.get(channel).getScheduledCommands().put(command, APIChannel.get(channel).getScheduler().schedule(task, delay, period, TimeUnit.MILLISECONDS));
    }
    public static void scheduleCommandInSeconds(String channel, String command, long delay,long period){
        Runnable task = new ScheduledCommand(channel,command);
        APIChannel.get(channel).getScheduledCommands().put(command, APIChannel.get(channel).getScheduler().schedule(task, delay, period, TimeUnit.SECONDS));
    }
    public static void scheduleCommandInMinutes(String channel, String command, long delay,long period){
        Runnable task = new ScheduledCommand(channel,command);
        APIChannel.get(channel).getScheduledCommands().put(command, APIChannel.get(channel).getScheduler().schedule(task, delay, period, TimeUnit.MINUTES));
    }
    public static void scheduleCommandInHours(String channel, String command, long delay,long period){
        Runnable task = new ScheduledCommand(channel,command);
        APIChannel.get(channel).getScheduledCommands().put(command, APIChannel.get(channel).getScheduler().schedule(task, delay, period, TimeUnit.HOURS));
    }
    public static void scheduleCommandInDays(String channel, String command, long delay,long period){
        Runnable task = new ScheduledCommand(channel,command);
        APIChannel.get(channel).getScheduledCommands().put(command, APIChannel.get(channel).getScheduler().schedule(task, delay, period, TimeUnit.DAYS));
    }

    public static void unScheduleCommand(String channel, String command){
        APIChannel.get(channel).getScheduledCommands().get(command).cancel(false);
    }
}
