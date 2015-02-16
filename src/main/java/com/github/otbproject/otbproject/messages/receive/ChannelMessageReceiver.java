package com.github.otbproject.otbproject.messages.receive;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.messages.send.MessageOut;
import com.github.otbproject.otbproject.proc.MessageProcessor;
import com.github.otbproject.otbproject.proc.ProcessedMessage;
import org.pircbotx.hooks.events.MessageEvent;

public class ChannelMessageReceiver implements Runnable {
    private final Channel channel;
    private MessageReceiveQueue queue;

    public ChannelMessageReceiver(Channel channel, MessageReceiveQueue queue) {
        this.channel = channel;
        this.queue = queue;
    }

    public void run() {
        MessageEvent event;

        try {
            while (true) {
                event = queue.take();
                String channelName = event.getChannel().getName().replace("#","");
                //TODO replace booleans with lookups
                ProcessedMessage processedMessage = MessageProcessor.process(this.channel.getDatabaseWrapper(), event.getMessage(), channelName, event.getUser().getNick(), false, false);
                String message = processedMessage.getResponse();
                if (!message.isEmpty()) {
                    MessageOut messageOut = new MessageOut(message);
                    this.channel.getSendQueue().add(messageOut);
                }
            }
        } catch (InterruptedException e) {
            // TODO tidy up
            App.logger.info("Stopped message receiver for " + channel.getName());
        }
    }
}
