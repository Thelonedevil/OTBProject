package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.util.BlockingHashSet;

import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CooldownSet extends BlockingHashSet {

    public boolean add(String name, int timeInSeconds) {
        if(super.add(name)){
            new Thread(new CooldownRemover(name, timeInSeconds, this)).start();
            return true;
        }
        return false;
    }

    /**
     *
     * Please do not use this method. It's only here because it's in the parent class.
     * @param s
     * @return
     */
    @Override
    @Deprecated
    public boolean add(String s) {
        return this.add(s, 10);
    }
}
