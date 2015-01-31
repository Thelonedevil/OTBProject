package com.github.opentwitchbotteam.otbproject.channels;

import com.github.opentwitchbotteam.otbproject.database.DatabaseHelper;
import com.github.opentwitchbotteam.otbproject.database.DatabaseWrapper;
import com.github.opentwitchbotteam.otbproject.messages.send.MessageSendQueue;
import com.github.opentwitchbotteam.otbproject.messages.send.ChannelMessageSender;
import com.github.opentwitchbotteam.otbproject.messages.send.NonexistentChannelException;

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
        catch (NonexistentChannelException e) {
            // TODO log
            e.printStackTrace();
            // cleanup
            MessageSendQueue.removeChannel(name);
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
        messageSenderThread.interrupt(); // TODO kill thread if this doesn't
        // TODO possibly set thread to null?
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
}
