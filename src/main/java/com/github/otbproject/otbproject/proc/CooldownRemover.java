package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.App;
import org.apache.logging.log4j.Level;

public class CooldownRemover implements Runnable {
    private static int increment = 1;
    private final String item;
    private final int waitInSeconds;
    private final CooldownSet cooldownSet;
    private Thread thread;

    public CooldownRemover(String item, int waitInSeconds, CooldownSet cooldownSet) {
        this.item = item;
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
            App.logger.info("Interrupted CooldownRemover for item '" + item + "'");
            App.logger.catching(Level.DEBUG, e);
        } catch (Exception e) {
            App.logger.catching(e);
        } finally {
            cooldownSet.nonInterruptingRemove(item);
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
