package com.github.otbproject.otbproject.command.scheduler;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channel.ChannelProxy;

import java.util.concurrent.TimeUnit;

public class ResetTask implements Runnable {

    private final ChannelProxy channel;
    private final String command;
    private final long delay;
    private final long period;
    private final TimeUnit timeUnit;
    private final ScheduledCommand scheduledCommand;

    public ResetTask(ChannelProxy channel, String command, long delay, long period, TimeUnit timeUnit) {
        this.channel = channel;
        this.command = command;
        this.delay = delay;
        this.period = period;
        this.timeUnit = timeUnit;
        scheduledCommand = new ScheduledCommand(channel, command);
    }

    @Override
    public void run() {
        channel.getScheduleManager().removeCommandFuture(command);
        try {
            channel.getScheduleManager().putCommandFuture(command, channel.getScheduler().schedule(scheduledCommand, delay, period, timeUnit));
            App.logger.debug("Reset timing of scheduled command '" + command + "' for the beginning of the hour");
        } catch (SchedulingException e) {
            App.logger.catching(e);
            App.logger.error("Removing ResetTask for command that could not be scheduled: " + command);
            channel.getScheduleManager().removeResetFuture(command);
        }
    }
}
