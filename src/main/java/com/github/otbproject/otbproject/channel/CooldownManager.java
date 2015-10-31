package com.github.otbproject.otbproject.channel;

import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

public class CooldownManager {
    private final ExpiringMap<String, Boolean> cooldownSet = ExpiringMap.builder()
            .variableExpiration()
            .expirationPolicy(ExpiringMap.ExpirationPolicy.CREATED)
            .build();
    private final Channel channel;
    private final ReadWriteLock channelLock;

    CooldownManager(Channel channel, ReadWriteLock lock) {
        this.channel = channel;
        channelLock = lock;
    }

    public boolean isOnCooldown(String s) {
        channelLock.readLock().lock();
        try {
            return cooldownSet.containsKey(s);
        } finally {
            channelLock.readLock().unlock();
        }
    }

    public boolean addCooldown(String s, int time) {
        channelLock.readLock().lock();
        try {
            if (channel.isInChannel()) {
                cooldownSet.put(s, Boolean.TRUE, time, TimeUnit.SECONDS);
                return true;
            }
            return false;
        } finally {
            channelLock.readLock().unlock();
        }
    }

    void clearCooldowns() {
        cooldownSet.clear();
    }
}
