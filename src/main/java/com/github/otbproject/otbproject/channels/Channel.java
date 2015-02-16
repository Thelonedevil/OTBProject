package com.github.otbproject.otbproject.channels;

import com.github.otbproject.otbproject.database.DatabaseHelper;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.send.MessageSendQueue;
import com.github.otbproject.otbproject.messages.send.ChannelMessageSender;

public class Channel {
    private String name;
    private DatabaseWrapper db;
    private MessageSendQueue queue;
    private ChannelMessageSender messageSender;
    private Thread messageSenderThread;
    private boolean inChannel;

    public Channel(String name) {
        this.name = name;
        this.inChannel = false;
    }

    public void join() {
        queue = new MessageSendQueue();
        messageSender = new ChannelMessageSender(name, queue);
        messageSenderThread = new Thread(messageSender);
        messageSenderThread.start();
        db = DatabaseHelper.getChannelDatabase(name);

        inChannel = true;

        // TODO trigger some ChannelJoinEvent
    }

    public void leave() {
        inChannel = false;

        db = null;
        messageSenderThread.interrupt();
        messageSenderThread = null;
        messageSender = null;
        queue = null;

        // TODO trigger some ChannelLeaveEvent
    }

    public String getName() {
        return name;
    }

    public boolean isInChannel() {
        return inChannel;
    }

    public DatabaseWrapper getDatabaseWrapper() {
        return db;
    }

    public MessageSendQueue getMessageSendQueue() {
        return queue;
    }
}
