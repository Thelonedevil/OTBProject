package com.github.otbproject.otbproject.channel;

import com.github.otbproject.otbproject.command.scheduler.ChannelScheduleManager;
import com.github.otbproject.otbproject.command.scheduler.Scheduler;
import com.github.otbproject.otbproject.config.ChannelConfig;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.database.SQLiteQuoteWrapper;
import com.github.otbproject.otbproject.filter.GroupFilterSet;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessageOut;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChannelProxy {
    private final Channel channel;

    public ChannelProxy(Channel channel) {
        this.channel = channel;
    }

    public boolean sendMessage(MessageOut messageOut) {
        return channel.sendMessage(messageOut);
    }

    public void clearSendQueue() {
        channel.clearSendQueue();
    }

    public boolean receiveMessage(PackagedMessage packagedMessage) {
        return channel.receiveMessage(packagedMessage);
    }

    public String getName() {
        return channel.getName();
    }

    public boolean isInChannel() {
        return channel.isInChannel();
    }

    public CooldownManager userCooldowns() {
        return channel.userCooldowns();
    }

    public CooldownManager commandCooldowns() {
        return channel.commandCooldowns();
    }

    public Set<String> getScheduledCommands() {
        return channel.getScheduledCommands();
    }

    public ChannelScheduleManager getScheduleManager() {
        return channel.getScheduleManager();
    }

    public DatabaseWrapper getMainDatabaseWrapper() {
        return channel.getMainDatabaseWrapper();
    }

    public SQLiteQuoteWrapper getQuoteDatabaseWrapper() {
        return channel.getQuoteDatabaseWrapper();
    }

    public <R> R getFromConfig(Function<ChannelConfig, R> function) {
        return channel.getFromConfig(function);
    }

    public void editConfig(Consumer<ChannelConfig> consumer) {
        channel.editConfig(consumer);
    }

    public Scheduler getScheduler() {
        return channel.getScheduler();
    }

    public ConcurrentMap<String, GroupFilterSet> getFilterMap() {
        return channel.getFilterMap();
    }

}
