package com.github.otbproject.otbproject.messages.send;

import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class MessageSendQueue {
    private final static HashMap<String, PriorityBlockingQueue<MessageOut>> queueMap = new HashMap<String, PriorityBlockingQueue<MessageOut>>();

    public static boolean hasChannel(String channel) {
        return queueMap.containsKey(channel);
    }

    private static void checkChannel(String channel) throws NonexistentChannelException {
        if (!queueMap.containsKey(channel)) {
            throw new NonexistentChannelException();
        }
    }

    // Returns false if channel already exists
    public static boolean addChannel(String channel) {
        if (hasChannel(channel)) {
            return false;
        }
        queueMap.put(channel, new PriorityBlockingQueue<MessageOut>());
        return true;
    }

    // Returns false if channel does not exist
    public static boolean removeChannel(String channel) {
        if (hasChannel(channel)) {
            queueMap.remove(channel);
            return true;
        }
        return false;
    }

    public static MessageOut take(String channel) throws NonexistentChannelException, InterruptedException {
        checkChannel(channel);
        return queueMap.get(channel).take();
    }

    public static void add(String channel, MessageOut message) throws NonexistentChannelException {
        checkChannel(channel);
        queueMap.get(channel).add(message);
    }

    public static void clear(String channel) throws NonexistentChannelException {
        checkChannel(channel);
        queueMap.get(channel).clear();
    }

    public static int size(String channel) throws NonexistentChannelException {
        checkChannel(channel);
        return queueMap.get(channel).size();
    }
}
