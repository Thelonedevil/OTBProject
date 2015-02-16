package com.github.otbproject.otbproject.channels;

import com.github.otbproject.otbproject.database.DatabaseHelper;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.receive.ChannelMessageReceiver;
import com.github.otbproject.otbproject.messages.receive.MessageReceiveQueue;
import com.github.otbproject.otbproject.messages.send.MessageSendQueue;
import com.github.otbproject.otbproject.messages.send.ChannelMessageSender;

public class Channel {
    private String name;
    private DatabaseWrapper db;
    private MessageSendQueue sendQueue;
    private ChannelMessageSender messageSender;
    private Thread messageSenderThread;
    private MessageReceiveQueue receiveQueue;
    private ChannelMessageReceiver messageReceiver;
    private Thread messageReceiverThread;
    private boolean inChannel;

    public Channel(String name) {
        this.name = name;
        this.inChannel = false;
    }

    public void join() {
        sendQueue = new MessageSendQueue();
        messageSender = new ChannelMessageSender(this, sendQueue);
        messageSenderThread = new Thread(messageSender);
        messageSenderThread.start();

        receiveQueue = new MessageReceiveQueue();
        messageReceiver = new ChannelMessageReceiver(this, receiveQueue);
        messageReceiverThread = new Thread(messageReceiver);
        messageReceiverThread.start();

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
        sendQueue = null;

        messageReceiverThread.interrupt();
        messageReceiverThread = null;
        messageReceiver = null;
        receiveQueue = null;

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

    public MessageSendQueue getSendQueue() {
        return sendQueue;
    }

    public MessageReceiveQueue getReceiveQueue() {
        return receiveQueue;
    }
}
