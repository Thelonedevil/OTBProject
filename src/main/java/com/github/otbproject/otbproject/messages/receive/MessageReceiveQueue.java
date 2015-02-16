package com.github.otbproject.otbproject.messages.receive;

import org.pircbotx.hooks.events.MessageEvent;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageReceiveQueue {
    private final LinkedBlockingQueue<MessageEvent> queue = new LinkedBlockingQueue<MessageEvent>();

    public MessageEvent take() throws InterruptedException {
        return queue.take();
    }

    public void add(MessageEvent message) {
        queue.add(message);
    }

    public void clear() {
        queue.clear();
    }

    public int size() {
        return queue.size();
    }
}
