package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.App;
import org.apache.logging.log4j.Level;

public class CooldownRemover implements Runnable {
    private String command;
    private int waitInSeconds;
    private CooldownSet cooldownSet;

    public CooldownRemover(String command, int waitInSeconds, CooldownSet cooldownSet) {
        this.command = command;
        this.waitInSeconds = waitInSeconds;
        this.cooldownSet = cooldownSet;
    }

    public void run() {
        try {
            Thread.currentThread().sleep(waitInSeconds * 1000);
        } catch (InterruptedException e) {
            App.logger.info("Interrupted CooldownRemover for command '" + command + "'");
            App.logger.catching(Level.DEBUG, e);
        } finally {
            cooldownSet.remove(command);
        }
    }
}
