package com.github.otbproject.otbproject.proc;

import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CooldownSet {
    private final HashSet<String> set = new HashSet<String>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public boolean contains(String name) {
        lock.readLock().lock();
        boolean contains;
        try {
            contains = set.contains(name);
        }
        finally {
            lock.readLock().unlock();
        }
        return contains;
    }

    public boolean add(String name, int timeInSeconds) {
        lock.writeLock().lock();
        boolean added;
        try {
            added = set.add(name);
        }
        finally {
            lock.writeLock().unlock();
        }
        new Thread(new CooldownRemover(name, timeInSeconds, this)).start();
        return added;
    }

    public boolean remove(String name) {
        lock.writeLock().lock();
        boolean removed;
        try {
            removed = set.remove(name);
        }
        finally {
            lock.writeLock().unlock();
        }
        return removed;
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            set.clear();
        }
        finally {
            lock.writeLock().unlock();
        }
    }
}
