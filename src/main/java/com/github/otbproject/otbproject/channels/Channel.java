package com.github.otbproject.otbproject.channels;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIDatabase;
import com.github.otbproject.otbproject.commands.scheduler.Scheduler;
import com.github.otbproject.otbproject.config.ChannelConfig;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.database.SQLiteQuoteWrapper;
import com.github.otbproject.otbproject.messages.receive.ChannelMessageReceiver;
import com.github.otbproject.otbproject.messages.receive.MessageReceiveQueue;
import com.github.otbproject.otbproject.messages.send.ChannelMessageSender;
import com.github.otbproject.otbproject.messages.send.MessageOut;
import com.github.otbproject.otbproject.messages.send.MessageSendQueue;
import com.github.otbproject.otbproject.proc.CooldownSet;
import com.github.otbproject.otbproject.util.BlockingHashSet;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Channel {
    private final MessageSendQueue sendQueue = new MessageSendQueue(this);
    public final MessageReceiveQueue receiveQueue = new MessageReceiveQueue();
    public final CooldownSet commandCooldownSet = new CooldownSet();
    public final CooldownSet userCooldownSet = new CooldownSet();
    public final BlockingHashSet subscriberStorage = new BlockingHashSet();
    private final String name;
    private final ChannelConfig config;
    private DatabaseWrapper mainDb;
    private SQLiteQuoteWrapper quoteDb;
    private ChannelMessageSender messageSender;
    private Thread messageSenderThread;
    private ChannelMessageReceiver messageReceiver;
    private Thread messageReceiverThread;
    private final Scheduler scheduler = new Scheduler();
    private final HashMap<String,ScheduledFuture> scheduledCommands = new HashMap<>();
    private final HashMap<String,ScheduledFuture> hourlyResetSchedules = new HashMap<>();
    private boolean inChannel;

    private final Lock lock = new ReentrantLock();

    public Channel(String name, ChannelConfig config) {
        this.name = name;
        this.config = config;
        this.inChannel = false;
    }

    public boolean join() {
        lock.lock();
        try {
            if (inChannel) {
                return false;
            }

            mainDb = APIDatabase.getChannelMainDatabase(name);
            if (mainDb == null) {
                App.logger.error("Unable to get main database for channel: " + name);
                return false;
            }

            quoteDb = APIDatabase.getChannelQuoteDatabase(name);
            if (quoteDb == null) {
                App.logger.error("Unable to get quote database for channel: " + name);
                return false;
            }

            messageSender = new ChannelMessageSender(this, sendQueue);
            messageSenderThread = new Thread(messageSender);
            messageSenderThread.start();

            messageReceiver = new ChannelMessageReceiver(this, receiveQueue);
            messageReceiverThread = new Thread(messageReceiver);
            messageReceiverThread.start();

            inChannel = true;

            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean leave() {
        lock.lock();
        try {
            if (!inChannel) {
                return false;
            }
            inChannel = false;

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
            subscriberStorage.clear();

            mainDb = null;

            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean sendMessage(MessageOut messageOut) {
        lock.lock();
        try {
            return inChannel && sendQueue.add(messageOut);
        } finally {
            lock.unlock();
        }

    }

    public String getName() {
        return name;
    }

    public boolean isInChannel() {
        lock.lock();
        try {
            return inChannel;
        } finally {
            lock.unlock();
        }
    }

    public DatabaseWrapper getMainDatabaseWrapper() {
        return mainDb;
    }

    public SQLiteQuoteWrapper getQuoteDatabaseWrapper() {
        return quoteDb;
    }

    public ChannelConfig getConfig() {
        return config;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public HashMap<String, ScheduledFuture> getScheduledCommands() {
        return scheduledCommands;
    }

    public HashMap<String, ScheduledFuture> getHourlyResetSchedules() {
        return hourlyResetSchedules;
    }
}
