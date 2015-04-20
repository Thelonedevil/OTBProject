package com.github.otbproject.otbproject.commands.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Justin on 20/04/2015.
 */
public class Scheduler {

    private final ScheduledExecutorService scheduledExecutorService;

    public Scheduler(){
        scheduledExecutorService = Executors.newScheduledThreadPool(5);
    }

    public ScheduledFuture<?> schedule(Runnable task, long delay, long period, TimeUnit timeUnit){
        return getScheduledExecutorService().scheduleAtFixedRate(task, delay, period, timeUnit);
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}
