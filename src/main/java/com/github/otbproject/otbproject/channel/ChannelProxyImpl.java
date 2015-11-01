package com.github.otbproject.otbproject.channel;

import com.github.otbproject.otbproject.command.scheduler.ChannelScheduleManager;
import com.github.otbproject.otbproject.command.scheduler.Scheduler;
import com.github.otbproject.otbproject.config.ChannelConfig;
import com.github.otbproject.otbproject.config.WrappedConfig;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.database.SQLiteQuoteWrapper;
import com.github.otbproject.otbproject.filter.GroupFilterSet;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessageOut;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;

class ChannelProxyImpl implements ChannelProxy {
    private final Channel channel;

    public ChannelProxyImpl(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean sendMessage(MessageOut messageOut) {
        return channel.sendMessage(messageOut);
    }

    @Override
    public void clearSendQueue() {
        channel.clearSendQueue();
    }

    @Override
    public boolean receiveMessage(PackagedMessage packagedMessage) {
        return channel.receiveMessage(packagedMessage);
    }

    @Override
    public String getName() {
        return channel.getName();
    }

    @Override
    public boolean isInChannel() {
        return channel.isInChannel();
    }

    @Override
    public CooldownManager userCooldowns() {
        return channel.userCooldowns();
    }

    @Override
    public CooldownManager commandCooldowns() {
        return channel.commandCooldowns();
    }

    @Override
    public Set<String> getScheduledCommands() {
        return channel.getScheduledCommands();
    }

    @Override
    public ChannelScheduleManager getScheduleManager() {
        return channel.getScheduleManager();
    }

    @Override
    public DatabaseWrapper getMainDatabaseWrapper() {
        return channel.getMainDatabaseWrapper();
    }

    @Override
    public SQLiteQuoteWrapper getQuoteDatabaseWrapper() {
        return channel.getQuoteDatabaseWrapper();
    }

    @Override
    public WrappedConfig<ChannelConfig> getConfig() {
        return channel.getConfig();
    }

    @Override
    public Scheduler getScheduler() {
        return channel.getScheduler();
    }

    @Override
    public ConcurrentMap<String, GroupFilterSet> getFilterMap() {
        return channel.getFilterMap();
    }

}
