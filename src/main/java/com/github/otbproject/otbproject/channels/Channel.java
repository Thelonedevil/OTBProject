package com.github.otbproject.otbproject.channels;

import com.github.otbproject.otbproject.config.ChannelConfig;
import com.github.otbproject.otbproject.database.DatabaseHelper;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.receive.ChannelMessageReceiver;
import com.github.otbproject.otbproject.messages.receive.MessageReceiveQueue;
import com.github.otbproject.otbproject.messages.send.MessageSendQueue;
import com.github.otbproject.otbproject.messages.send.ChannelMessageSender;
import com.github.otbproject.otbproject.proc.CooldownSet;

public class Channel {
    private final String name;
    private ChannelConfig config;
    private DatabaseWrapper db;
    private ChannelMessageSender messageSender;
    private Thread messageSenderThread;
    private ChannelMessageReceiver messageReceiver;
    private Thread messageReceiverThread;
    private boolean inChannel;
    public final MessageSendQueue sendQueue = new MessageSendQueue(this);
    public final MessageReceiveQueue receiveQueue = new MessageReceiveQueue();
    public final CooldownSet commandCooldownSet = new CooldownSet();
    public final CooldownSet userCooldownSet = new CooldownSet();

    public Channel(String name, ChannelConfig config) {
        this.name = name;
        this.config = config;
        this.inChannel = false;
    }

    public void join() {
        messageSender = new ChannelMessageSender(this, sendQueue);
        messageSenderThread = new Thread(messageSender);
        messageSenderThread.start();

        messageReceiver = new ChannelMessageReceiver(this, receiveQueue);
        messageReceiverThread = new Thread(messageReceiver);
        messageReceiverThread.start();

        db = DatabaseHelper.getChannelDatabase(name);

        inChannel = true;
    }

    public void leave() {
        inChannel = false;

        db = null;

        messageSenderThread.interrupt();
        messageSenderThread = null;
        messageSender = null;
        sendQueue.clear();

        messageReceiverThread.interrupt();
        messageReceiverThread = null;
        messageReceiver = null;
        receiveQueue.clear();

        commandCooldownSet.clear();
        userCooldownSet.clear();
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

    public ChannelConfig getConfig() {
        return config;
    }
}
