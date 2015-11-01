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

public interface ChannelProxy {
    boolean sendMessage(MessageOut messageOut);

    void clearSendQueue();

    boolean receiveMessage(PackagedMessage packagedMessage);

    String getName();

    boolean isInChannel();

    CooldownManager userCooldowns();

    CooldownManager commandCooldowns();

    Set<String> getScheduledCommands();

    ChannelScheduleManager getScheduleManager();

    DatabaseWrapper getMainDatabaseWrapper();

    SQLiteQuoteWrapper getQuoteDatabaseWrapper();

    <R> R getFromConfig(Function<ChannelConfig, R> function);

    void editConfig(Consumer<ChannelConfig> consumer);

    Scheduler getScheduler();

    ConcurrentMap<String, GroupFilterSet> getFilterMap();
}
