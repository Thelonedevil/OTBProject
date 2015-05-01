package com.github.otbproject.otbproject.scripts;

import groovy.lang.Script;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ScriptCache {
    private final HashMap<String, Script> map = new HashMap<String, Script>();
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
        Script previousScript;
        try {
            previousScript = map.put(string, script);
        } finally {
            lock.writeLock().unlock();
        }
        return previousScript;
    }

    public Script remove(String s) {
        lock.writeLock().lock();
        Script removed;
        try {
            removed = map.remove(s);
        } finally {
            lock.writeLock().unlock();
        }
        return removed;
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
