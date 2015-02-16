package com.github.otbproject.otbproject.messages.send;

import com.github.otbproject.otbproject.App;

public class ChannelMessageSender implements Runnable {
    private final String channel;
    private MessageSendQueue queue;

    public ChannelMessageSender(String channel, MessageSendQueue queue) {
        this.channel = channel;
        this.queue = queue;
    }

    public void run() {
        MessageOut message;

        try {
            while (true) {
                message = queue.take();
                SendingWrapper.send(channel, message.getMessage());
                Thread.sleep(2000); // TODO store as constant somewhere
            }
        } catch (InterruptedException e) {
            // TODO tidy up
            App.logger.info("Stopped message sender for queue " + channel);
        }
    }
}
