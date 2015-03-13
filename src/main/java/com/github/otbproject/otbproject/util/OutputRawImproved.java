package com.github.otbproject.otbproject.util;

import com.github.otbproject.otbproject.App;
import org.pircbotx.PircBotX;
import org.pircbotx.Utils;
import org.pircbotx.output.OutputRaw;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Justin on 13/03/2015.
 */
public class OutputRawImproved extends OutputRaw {
    public OutputRawImproved(PircBotX bot) {
        super(bot);
    }

    /**
     * Sends a raw line to the IRC server as soon as possible
     * @param line The raw line to send to the IRC server
     * @param resetDelay If true, pending messages will reset their delay.
     */
    public void rawLineNow(String line, boolean resetDelay) {
        checkNotNull(line, "Line cannot be null");
        if (!bot.isConnected())
            throw new RuntimeException("Not connected to server");
        writeLock.lock();
        try {
            if(!line.contains("PASS ")){
                App.logger.info(line);
            }
            Utils.sendRawLineToServer(bot, line);
            lastSentLine = System.nanoTime();
            if (resetDelay)
                //Reset the
                writeNowCondition.signalAll();
        } finally {
            writeLock.unlock();
        }
    }
}
