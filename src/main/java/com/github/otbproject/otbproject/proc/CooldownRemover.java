package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.App;
import org.apache.logging.log4j.Level;

public class CooldownRemover<T> implements Runnable {
    private static int increment = 1;
    private final T t;
    private final int waitInSeconds;
    private final CooldownSet<T> cooldownSet;
    private Thread thread;

    public CooldownRemover(T t, int waitInSeconds, CooldownSet<T> cooldownSet) {
        this.t = t;
        this.waitInSeconds = waitInSeconds;
        this.cooldownSet = cooldownSet;
        this.thread = null;
    }

    public void run() {
        Thread.currentThread().setName("Cooldown Remover " + increment++);
        thread = Thread.currentThread();
        try {
            Thread.sleep(waitInSeconds * 1000);
        } catch (InterruptedException e) {
            App.logger.info("Interrupted CooldownRemover for item '" + t + "'");
            App.logger.catching(Level.DEBUG, e);
        } catch (Exception e) {
            App.logger.catching(e);
        } finally {
            cooldownSet.nonInterruptingRemove(t);
        }
    }

    public void interrupt() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public int getWaitInSeconds() {
        return waitInSeconds;
    }
}
