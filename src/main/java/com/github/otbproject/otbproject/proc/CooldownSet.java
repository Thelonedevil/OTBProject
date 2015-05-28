package com.github.otbproject.otbproject.proc;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CooldownSet<T> {
    private final HashMap<T, CooldownRemover<T>> map = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public boolean contains(T t) {
        lock.readLock().lock();
        try {
            return map.containsKey(t);
        } finally {
            lock.readLock().unlock();
        }
    }

    public CooldownRemover<T> getCooldownRemover(T t) {
        lock.readLock().lock();
        try {
            return map.get(t);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     *
     * @param t element to add to the set
     * @param timeInSeconds time in seconds until t should be removed from the set
     * @return <tt>true</tt> if set does not already contain t
     */
    public boolean add(T t, int timeInSeconds) {
        lock.writeLock().lock();
        try {
            if (map.containsKey(t)) {
                return false;
            }
            CooldownRemover<T> cooldownRemover = new CooldownRemover<>(t, timeInSeconds, this);
            map.put(t, cooldownRemover);
            new Thread(cooldownRemover).start();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean nonInterruptingRemove(T t) {
        lock.writeLock().lock();
        try {
            return (map.remove(t) != null);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean remove(T t) {
        CooldownRemover cooldownRemover = getCooldownRemover(t);
        if (cooldownRemover == null) {
            return false;
        }

        // Interrupting the cooldown remover automatically removes the t from the set
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
