package com.github.otbproject.otbproject.messages.send;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;

public class ChannelMessageSender implements Runnable {
    private final Channel channel;
    private MessageSendQueue queue;

    public ChannelMessageSender(Channel channel, MessageSendQueue queue) {
        this.channel = channel;
        this.queue = queue;
    }

    public void run() {
        MessageOut message;

        try {
            while (true) {
                message = queue.take();
                SendingWrapper.send(channel.getName(), message.getMessage());
                Thread.sleep(2000); // TODO store as constant somewhere
            }
        } catch (InterruptedException e) {
            // TODO tidy up
            App.logger.info("Stopped message sender for " + channel.getName());
        }
    }
}
