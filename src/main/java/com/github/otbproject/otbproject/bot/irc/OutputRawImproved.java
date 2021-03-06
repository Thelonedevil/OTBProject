package com.github.otbproject.otbproject.bot.irc;

import com.github.otbproject.otbproject.App;
import org.pircbotx.PircBotX;
import org.pircbotx.Utils;
import org.pircbotx.output.OutputRaw;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

class OutputRawImproved extends OutputRaw {
    public OutputRawImproved(PircBotX bot) {
        super(bot);
    }

    /**
     * Sends a raw line to the IRC server as soon as possible
     *
     * @param line       The raw line to send to the IRC server
     * @param resetDelay If true, pending messages will reset their delay.
     */
    public void rawLineNow(String line, boolean resetDelay) {
        checkNotNull(line, "Line cannot be null");
        if (!bot.isConnected())
            throw new RuntimeException("Not connected to server");
        writeLock.lock();
        try {
            if (!line.contains("PASS ")) {
                App.logger.info(line);
            }
            Utils.sendRawLineToServer(bot, line);
            lastSentLine = System.nanoTime();
            if (resetDelay)
                //Reset the
                writeNowCondition.signalAll();
        } catch (IOException var8) {
            throw new RuntimeException("IO exception when sending line to server, is the network still up? " + this.exceptionDebug(), var8);
        } catch (Exception var9) {
            throw new RuntimeException("Could not send line to server. " + this.exceptionDebug(), var9);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Sends a raw line through the outgoing message queue.
     *
     * @param line The raw line to send to the IRC server.
     */
    public void rawLine(String line) {
        checkNotNull(line, "Line cannot be null");
        if (!bot.isConnected())
            throw new RuntimeException("Not connected to server");
        writeLock.lock();
        try {
            //Block until we can send, taking into account a changing lastSentLine
            long curNanos = System.nanoTime();
            while (lastSentLine + delayNanos > curNanos) {
                writeNowCondition.await(lastSentLine + delayNanos - curNanos, TimeUnit.NANOSECONDS);
                curNanos = System.nanoTime();
            }
            if (!line.startsWith("PONG")) {
                App.logger.info(line);
            }
            Utils.sendRawLineToServer(bot, line);
            lastSentLine = System.nanoTime();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't pause thread for message delay", e);
        } finally {
            writeLock.unlock();
        }
    }
}
