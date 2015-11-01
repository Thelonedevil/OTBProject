package com.github.otbproject.otbproject.channel;

import com.github.otbproject.otbproject.bot.Control;
import com.github.otbproject.otbproject.command.scheduler.ChannelScheduleManager;
import com.github.otbproject.otbproject.command.scheduler.Scheduler;
import com.github.otbproject.otbproject.command.scheduler.Schedules;
import com.github.otbproject.otbproject.config.ChannelConfig;
import com.github.otbproject.otbproject.config.UpdatingConfig;
import com.github.otbproject.otbproject.config.WrappedConfig;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.database.Databases;
import com.github.otbproject.otbproject.database.SQLiteQuoteWrapper;
import com.github.otbproject.otbproject.filter.GroupFilterSet;
import com.github.otbproject.otbproject.messages.receive.ChannelMessageProcessor;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.ChannelMessageSender;
import com.github.otbproject.otbproject.messages.send.MessageOut;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Channel implements ChannelProxy {
    private CooldownManager commandCooldownManager;
    private CooldownManager userCooldownManager;
    private final String name;
    private final UpdatingConfig<ChannelConfig> config;
    private final WrappedConfig<ChannelConfig> configProxy;
    private final DatabaseWrapper mainDb;
    private final SQLiteQuoteWrapper quoteDb;
    private ChannelMessageSender messageSender;
    private ChannelMessageProcessor messageProcessor;
    private final Scheduler scheduler;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledCommands = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ScheduledFuture<?>> hourlyResetSchedules = new ConcurrentHashMap<>();
    private ConcurrentMap<String, GroupFilterSet> filterMap;
    private boolean inChannel;

    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final ChannelScheduleManager scheduleManager = new ChannelScheduleManager(scheduledCommands, hourlyResetSchedules, lock);

    private Channel(String name, UpdatingConfig<ChannelConfig> config) throws ChannelInitException {
        this.name = name;
        this.config = config;
        configProxy = config.asWrappedConfig();
        inChannel = false;
        scheduler = new Scheduler(name);

        mainDb = Databases.createChannelMainDbWrapper(name);
        if (mainDb == null) {
            throw new ChannelInitException(name, "Unable to get main database");
        }
        quoteDb = Databases.createChannelQuoteDbWrapper(name);
        if (quoteDb == null) {
            throw new ChannelInitException(name, "Unable to get quote database");
        }

        //filterMap = GroupFilterSet.createGroupFilterSetMap(FilterGroups.getFilterGroups(mainDb), Filters.getAllFilters(mainDb));
    }

    private void init() {
        messageSender = new ChannelMessageSender(this);
        messageProcessor = new ChannelMessageProcessor(this);
        commandCooldownManager = new CooldownManager(this, lock);
        userCooldownManager = new CooldownManager(this, lock);
    }

    public static Channel create(String name, UpdatingConfig<ChannelConfig> config) throws ChannelInitException {
        Channel channel = new Channel(name, config);
        channel.init();
        return channel;
    }

    public boolean join() {
        lock.writeLock().lock();
        try {
            if (inChannel) {
                return false;
            }

            config.startMonitoring();

            messageSender.start();
            scheduler.start();
            Schedules.loadFromDatabase(this);

            inChannel = true;

            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean leave() {
        lock.writeLock().lock();
        try {
            if (!inChannel) {
                return false;
            }
            inChannel = false;

            config.stopMonitoring();

            messageSender.stop();
            scheduler.stop();
            scheduledCommands.clear();
            hourlyResetSchedules.clear();

            commandCooldownManager.clearCooldowns();
            userCooldownManager.clearCooldowns();

            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean sendMessage(MessageOut messageOut) {
        lock.readLock().lock();
        try {
            return inChannel && !config.get(ChannelConfig::isSilenced) && messageSender.send(messageOut);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clearSendQueue() {
        lock.readLock().lock();
        try {
            messageSender.clearQueue();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Not concurrent.
     * Cannot read-lock because:
     * * It may execute a script and may consequently take an extended time to execute
     * * It may execute a script which requires a write-lock (such as one to leave
     * the channel), which will cause it to lock up.
     * <p>
     * Checks if in the channel only initially, and then attempts to process the
     * message. Some calls from within messageProcessor.process() may fail if
     * the bot leaves the channel while it is still executing.
     *
     * @param packagedMessage a message to receive and process
     * @return A boolean stating whether it is likely that the message was processed
     * successfully. Should not be relied upon to be accurate
     */
    @Override
    public boolean receiveMessage(PackagedMessage packagedMessage) {
        if (inChannel) {
            messageProcessor.process(packagedMessage);
        } else {
            return false; // In case joins channel since the 'if' statement
        }
        return inChannel;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isInChannel() {
        lock.readLock().lock();
        try {
            return inChannel;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public CooldownManager userCooldowns() {
        return userCooldownManager;
    }

    @Override
    public CooldownManager commandCooldowns() {
        return commandCooldownManager;
    }

    @Deprecated
    public boolean isUserCooldown(String user) {
        return userCooldownManager.isOnCooldown(user);
    }

    @Deprecated
    public boolean addUserCooldown(String user, int time) {
        return userCooldownManager.addCooldown(user, time);
    }

    @Deprecated
    public boolean isCommandCooldown(String user) {
        return commandCooldownManager.isOnCooldown(user);
    }

    @Deprecated
    public boolean addCommandCooldown(String user, int time) {
        return commandCooldownManager.addCooldown(user, time);
    }

    @Override
    public Set<String> getScheduledCommands() {
        return Collections.unmodifiableSet(scheduledCommands.keySet());
    }

    @Override
    public ChannelScheduleManager getScheduleManager() {
        return scheduleManager;
    }

    @Deprecated
    public void putCommandFuture(String command, ScheduledFuture<?> future) {
        lock.readLock().lock();
        try {
            scheduledCommands.put(command, future);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Deprecated
    public boolean hasCommandFuture(String command) {
        return scheduledCommands.containsKey(command);
    }

    @Deprecated
    public boolean removeCommandFuture(String command) {
        ScheduledFuture<?> future;
        lock.readLock().lock();
        try {
            future = scheduledCommands.remove(command);
        } finally {
            lock.readLock().unlock();
        }
        return (future != null) && future.cancel(true);
    }

    @Deprecated
    public void putResetFuture(String command, ScheduledFuture<?> future) {
        lock.readLock().lock();
        try {
            hourlyResetSchedules.put(command, future);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Deprecated
    public boolean hasResetFuture(String command) {
        return hourlyResetSchedules.containsKey(command);
    }

    @Deprecated
    public boolean removeResetFuture(String command) {
        ScheduledFuture<?> future;
        lock.readLock().lock();
        try {
            future = hourlyResetSchedules.remove(command);
        } finally {
            lock.readLock().unlock();
        }
        return (future != null) && future.cancel(true);
    }

    @Override
    public DatabaseWrapper getMainDatabaseWrapper() {
        return mainDb;
    }

    @Override
    public SQLiteQuoteWrapper getQuoteDatabaseWrapper() {
        return quoteDb;
    }

    @Override
    public WrappedConfig<ChannelConfig> getConfig() {
        return configProxy;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public ConcurrentMap<String, GroupFilterSet> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(ConcurrentMap<String, GroupFilterSet> filterMap) {
        this.filterMap = filterMap;
    }

    public ChannelProxy asProxy() {
        return new Proxy();
    }

    public static boolean isBotChannel(String channel) {
        return channel.equalsIgnoreCase(Control.getBot().getUserName());
    }

    public static boolean isBotChannel(ChannelProxy channel) {
        return isBotChannel(channel.getName());
    }

    private class Proxy implements ChannelProxy {
        private Proxy() {
        }

        @Override
        public boolean sendMessage(MessageOut messageOut) {
            return Channel.this.sendMessage(messageOut);
        }

        @Override
        public void clearSendQueue() {
            Channel.this.clearSendQueue();
        }

        @Override
        public boolean receiveMessage(PackagedMessage packagedMessage) {
            return Channel.this.receiveMessage(packagedMessage);
        }

        @Override
        public String getName() {
            return Channel.this.getName();
        }

        @Override
        public boolean isInChannel() {
            return Channel.this.isInChannel();
        }

        @Override
        public CooldownManager userCooldowns() {
            return Channel.this.userCooldowns();
        }

        @Override
        public CooldownManager commandCooldowns() {
            return Channel.this.commandCooldowns();
        }

        @Override
        public Set<String> getScheduledCommands() {
            return Channel.this.getScheduledCommands();
        }

        @Override
        public ChannelScheduleManager getScheduleManager() {
            return Channel.this.getScheduleManager();
        }

        @Override
        public DatabaseWrapper getMainDatabaseWrapper() {
            return Channel.this.getMainDatabaseWrapper();
        }

        @Override
        public SQLiteQuoteWrapper getQuoteDatabaseWrapper() {
            return Channel.this.getQuoteDatabaseWrapper();
        }

        @Override
        public WrappedConfig<ChannelConfig> getConfig() {
            return Channel.this.getConfig();
        }

        @Override
        public Scheduler getScheduler() {
            return Channel.this.getScheduler();
        }

        @Override
        public ConcurrentMap<String, GroupFilterSet> getFilterMap() {
            return Channel.this.getFilterMap();
        }
    }
}
