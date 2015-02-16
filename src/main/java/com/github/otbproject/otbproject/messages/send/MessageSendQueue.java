package com.github.otbproject.otbproject.messages.send;

import java.util.concurrent.PriorityBlockingQueue;

public class MessageSendQueue {
    private final PriorityBlockingQueue<MessageOut> queue = new PriorityBlockingQueue<>();

    public MessageOut take() throws InterruptedException {
        return queue.take();
    }

    public void add(MessageOut message) {
        queue.add(message);
    }

    public void clear() {
        queue.clear();
    }

    public int size() {
        return queue.size();
    }
}
