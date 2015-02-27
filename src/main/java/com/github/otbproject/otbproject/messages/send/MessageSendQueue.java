package com.github.otbproject.otbproject.messages.send;

import com.github.otbproject.otbproject.channels.Channel;

import java.util.concurrent.PriorityBlockingQueue;

public class MessageSendQueue {
    private final PriorityBlockingQueue<MessageOut> queue = new PriorityBlockingQueue<>();
    private final Channel channel;

    public MessageSendQueue(Channel channel) {
        this.channel = channel;
    }

    public MessageOut take() throws InterruptedException {
        return queue.take();
    }

    public boolean add(MessageOut message) {
        MessagePriority priority = message.getPriority();
        // Defaults to no limit
        int limit = -1;

        if (priority == MessagePriority.HIGH) {
            limit = channel.getConfig().queueLimits.getHighPriorityLimit();
        }
        else if (priority == MessagePriority.DEFAULT) {
            limit = channel.getConfig().queueLimits.getDefaultPriorityLimit();
        }
        else if (priority == MessagePriority.LOW) {
            limit = channel.getConfig().queueLimits.getLowPriorityLimit();
        }

        if ((limit >= 0) && queue.size() > limit) {
            return false;
        }

        return queue.add(message);
    }

    public void clear() {
        queue.clear();
    }

    public int size() {
        return queue.size();
    }
}
