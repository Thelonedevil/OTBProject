package com.github.otbproject.otbproject.channels;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.database.DatabaseHelper;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.send.MessageSendQueue;
import com.github.otbproject.otbproject.messages.send.ChannelMessageSender;
import com.github.otbproject.otbproject.messages.send.NonexistentChannelException;

public class Channel {
    private String name;
    private DatabaseWrapper db;
    private ChannelMessageSender messageSender;
    private Thread messageSenderThread;
    private boolean inChannel;

    public Channel(String name) {
        this.name = name;
        this.inChannel = false;
    }

    public void join() {
        MessageSendQueue.addChannel(name);
        try {
            messageSender = new ChannelMessageSender(name);
        }
        // shouldn't happen
        catch (NonexistentChannelException e) {
            // TODO log more info
            App.logger.catching(e);
            // TODO throw some sort of exception
        }
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
        MessageSendQueue.removeChannel(name);

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
}
