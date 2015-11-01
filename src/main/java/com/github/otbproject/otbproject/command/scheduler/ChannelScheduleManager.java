package com.github.otbproject.otbproject.command.scheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReadWriteLock;

public class ChannelScheduleManager {
    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledCommands;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> hourlyResetSchedules;
    private final ReadWriteLock channelLock;

    public ChannelScheduleManager(ConcurrentHashMap<String, ScheduledFuture<?>> scheduledCommands,
                                  ConcurrentHashMap<String, ScheduledFuture<?>> hourlyResetSchedules,
                                  ReadWriteLock channelLock) {
        this.scheduledCommands = scheduledCommands;
        this.hourlyResetSchedules = hourlyResetSchedules;
        this.channelLock = channelLock;
    }

    void putCommandFuture(String command, ScheduledFuture<?> future) {
        channelLock.readLock().lock();
        try {
            scheduledCommands.put(command, future);
        } finally {
            channelLock.readLock().unlock();
        }
    }

    boolean hasCommandFuture(String command) {
        return scheduledCommands.containsKey(command);
    }

    boolean removeCommandFuture(String command) {
        ScheduledFuture<?> future;
        channelLock.readLock().lock();
        try {
            future = scheduledCommands.remove(command);
        } finally {
            channelLock.readLock().unlock();
        }
        return (future != null) && future.cancel(true);
    }

    void putResetFuture(String command, ScheduledFuture<?> future) {
        channelLock.readLock().lock();
        try {
            hourlyResetSchedules.put(command, future);
        } finally {
            channelLock.readLock().unlock();
        }
    }

    boolean hasResetFuture(String command) {
        return hourlyResetSchedules.containsKey(command);
    }

    boolean removeResetFuture(String command) {
        ScheduledFuture<?> future;
        channelLock.readLock().lock();
        try {
            future = hourlyResetSchedules.remove(command);
        } finally {
            channelLock.readLock().unlock();
        }
        return (future != null) && future.cancel(true);
    }
}
