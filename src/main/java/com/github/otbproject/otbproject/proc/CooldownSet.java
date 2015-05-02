package com.github.otbproject.otbproject.proc;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CooldownSet {
    private final HashMap<String, CooldownRemover> map = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public boolean contains(String item) {
        lock.readLock().lock();
        try {
            return map.containsKey(item);
        } finally {
            lock.readLock().unlock();
        }
    }

    public CooldownRemover getCooldownRemover(String item) {
        lock.readLock().lock();
        try {
            return map.get(item);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     *
     * @param item item to add to the set
     * @param timeInSeconds time in seconds until item should be removed from the set
     * @return <tt>true</tt> if set does not already contain item
     */
    public boolean add(String item, int timeInSeconds) {
        lock.writeLock().lock();
        try {
            if (map.containsKey(item)) {
                return false;
            }
            CooldownRemover cooldownRemover = new CooldownRemover(item, timeInSeconds, this);
            map.put(item, cooldownRemover);
            new Thread(cooldownRemover).start();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean nonInterruptingRemove(String item) {
        lock.writeLock().lock();
        try {
            return (map.remove(item) != null);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean remove(String item) {
        CooldownRemover cooldownRemover = getCooldownRemover(item);
        if (cooldownRemover == null) {
            return false;
        }

        // Interrupting the cooldown remover automatically removes the item from the set
        cooldownRemover.interrupt();
        return true;
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            map.values().forEach(CooldownRemover::interrupt);
            map.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
