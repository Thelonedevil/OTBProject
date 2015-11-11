package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.channel.ChannelManager;
import com.github.otbproject.otbproject.channel.ProxiedChannel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.database.Databases;
import com.github.otbproject.otbproject.event.ChannelMessageEvent;
import com.github.otbproject.otbproject.util.Watcher;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public abstract class AbstractBot implements Bot {
    private final ConcurrentMap<String, ProxiedChannel> channels;
    private final ChannelManager channelManager;
    protected final DatabaseWrapper botDB = Databases.createBotDbWrapper();
    private final EventBus eventBus;

    public AbstractBot() {
        eventBus = new EventBus((exception, context) -> {
            App.logger.catching(exception);
            Watcher.logException();
        });

        eventBus.register(new MessageEventHandler());
        channels = new ConcurrentHashMap<>();
        channelManager = new ChannelManager(channels);
    }

    @Override
    @Deprecated
    public ConcurrentMap<String, Channel> getChannels() {
        return channels.entrySet().stream().collect(Collectors.toConcurrentMap(Map.Entry::getKey, entry -> entry.getValue().channel()));
    }

    @Override
    public ChannelManager channelManager() {
        return channelManager;
    }

    @Override
    public DatabaseWrapper getBotDB() {
        return botDB;
    }

    /**
     * Any class extending this one which overrides this method SHOULD call
     * this method at the end of the body of the overriding method
     */
    @Override
    public void shutdown() {
        channels.values().forEach(proxiedChannel -> proxiedChannel.channel().leave());
        channels.clear();
    }

    @Override
    public EventBus eventBus() {
        return eventBus;
    }

    private static class MessageEventHandler {
        @Subscribe
        public void receiveMessage(ChannelMessageEvent event) {
            if (!event.isFiltered()) {
                event.getChannel().receiveMessage(event.getMessage());
            }
        }
    }
}
