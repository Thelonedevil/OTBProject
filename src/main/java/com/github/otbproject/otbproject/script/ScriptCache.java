package com.github.otbproject.otbproject.script;

import groovy.lang.Script;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ScriptCache {
    private final HashMap<String, Script> map = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public boolean contains(String s) {
        lock.readLock().lock();
        boolean contains;
        try {
            contains = map.containsKey(s);
        } finally {
            lock.readLock().unlock();
        }
        return contains;
    }

    public Script get(String string) {
        lock.readLock().lock();
        Script script;
        try {
            script = map.get(string);
        } finally {
            lock.readLock().unlock();
        }
        return script;
    }

    public Script put(String string, Script script) {
        lock.writeLock().lock();
        try {
            return map.put(string, script);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Script remove(String s) {
        lock.writeLock().lock();
        try {
            return map.remove(s);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            map.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
