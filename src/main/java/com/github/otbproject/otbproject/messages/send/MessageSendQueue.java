package com.github.otbproject.otbproject.messages.send;

import com.github.otbproject.otbproject.channels.Channel;
import com.google.common.collect.MinMaxPriorityQueue;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessageSendQueue {
    private final MinMaxPriorityQueue<MessageOut> deque = MinMaxPriorityQueue.create();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Channel channel;

    public MessageSendQueue(Channel channel) {
        this.channel = channel;
    }

    public MessageOut take() throws InterruptedException {
        lock.writeLock().lock();
        try {
            return deque.removeFirst();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean add(MessageOut message) {
        if (channel.getConfig().isSilenced()) {
            return false;
        }

        MessagePriority priority = message.getPriority();
        int limit = getPriorityLimit(priority);

        if ((limit >= 0) && deque.size() > limit) {
            return false;
        }

        lock.writeLock().lock();
        try {
            deque.add(message);
            prune();
        } finally {
            lock.writeLock().unlock();
        }
        return true;
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            deque.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return deque.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    // MUST CALL FROM WITHIN LOCKED CODE BLOCK
    private void prune() {
        int limit;

        while (true) {
            // Get limit of last element
            limit = getPriorityLimit(deque.peekLast().getPriority());

            // Remove last element if size is larger than limit for that element
            if ((limit >=0) && deque.size() > limit) {
                deque.removeLast();
            } else {
                break;
            }
        }
    }

    private int getPriorityLimit(MessagePriority priority) {
        // Defaults to no limit
        int limit = -1;

        if (priority == MessagePriority.HIGH) {
            limit = channel.getConfig().queueLimits.getHighPriorityLimit();
        } else if (priority == MessagePriority.DEFAULT) {
            limit = channel.getConfig().queueLimits.getDefaultPriorityLimit();
        } else if (priority == MessagePriority.LOW) {
            limit = channel.getConfig().queueLimits.getLowPriorityLimit();
        }

        return limit;
    }
}
