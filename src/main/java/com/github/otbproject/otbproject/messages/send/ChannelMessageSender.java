package com.github.otbproject.otbproject.messages.send;

import com.github.otbproject.otbproject.App;

public class ChannelMessageSender implements Runnable {
    private String channel;

    public ChannelMessageSender(String channel) throws NonexistentChannelException {
        this.channel = channel;
    }

    public void run() {
        MessageOut message;

        try {
            while (true) {
                message = MessageSendQueue.take(channel);
                // TODO send message
                Thread.sleep(2000); // TODO store as constant somewhere
            }
        } catch (InterruptedException e) {
            // TODO tidy up
            App.logger.info("Stopped message sender for channel " + channel);
        }
        // This shouldn't happen
        catch (NonexistentChannelException e) {
            // TODO log more info
            App.logger.catching(e);
        }
    }
}
