package com.github.otbproject.otbproject.util;

import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BlockingHashSet {
    private final HashSet<String> set = new HashSet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public boolean contains(String s) {
        lock.readLock().lock();
        boolean contains;
        try {
            contains = set.contains(s);
        } finally {
            lock.readLock().unlock();
        }
        return contains;
    }

    public boolean add(String s) {
        lock.writeLock().lock();
        boolean added;
        try {
            added = set.add(s);
        } finally {
            lock.writeLock().unlock();
        }
        return added;
    }

    public boolean remove(String s) {
        lock.writeLock().lock();
        boolean removed;
        try {
            removed = set.remove(s);
        } finally {
            lock.writeLock().unlock();
        }
        return removed;
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            set.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
