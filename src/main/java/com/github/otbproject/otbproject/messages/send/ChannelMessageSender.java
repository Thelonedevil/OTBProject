package com.github.otbproject.otbproject.messages.send;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIConfig;
import com.github.otbproject.otbproject.channels.Channel;

public class ChannelMessageSender implements Runnable {
    private final Channel channel;
    private MessageSendQueue queue;

    public ChannelMessageSender(Channel channel, MessageSendQueue queue) {
        this.channel = channel;
        this.queue = queue;
    }

    public void run() {
        try {
            Thread.currentThread().setName(channel.getName() + " Message Sender");
            MessageOut message;

            while (true) {
                message = queue.take();
                SendingWrapper.send(channel.getName(), message.getMessage());
                Thread.sleep(APIConfig.getBotConfig().getMessageSendDelayInMilliseconds());
            }
        } catch (InterruptedException e) {
            App.logger.info("Stopped message sender for " + channel.getName());
        } catch (Exception e) {
            App.logger.catching(e);
        }
    }
}
