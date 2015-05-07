package com.github.otbproject.otbproject.commands.scheduler;

import com.github.otbproject.otbproject.api.APISchedule;

import java.util.concurrent.TimeUnit;

public class ResetTask implements Runnable {

    private final String channel;
    private final String command;
    private final long delay;
    private final long period;
    private final TimeUnit timeUnit;

    public ResetTask(String channel, String command, long delay,long period,TimeUnit timeUnit){
        this.channel = channel;
        this.command = command;
        this.delay = delay;
        this.period = period;
        this.timeUnit = timeUnit;
    }

    @Override
    public void run() {
        APISchedule.unScheduleCommand(channel,command);
        if (timeUnit.equals(TimeUnit.MINUTES)){
            APISchedule.scheduleCommandInMinutes(channel,command,delay,period,false);
        }else if (timeUnit.equals(TimeUnit.SECONDS)){
            APISchedule.scheduleCommandInSeconds(channel,command,delay,period,false);
        }
    }
}
