package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.channel.ChannelManager;
import com.github.otbproject.otbproject.channel.ProxiedChannel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.database.Databases;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.receive.MessageHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public abstract class AbstractBot implements Bot {
    private final ConcurrentMap<String, ProxiedChannel> channels;
    private final ChannelManager channelManager;
    protected final DatabaseWrapper botDB = Databases.createBotDbWrapper();
    protected MessageHandler messageHandlers = (channel, packagedMessage, timedOut) -> {};

    public AbstractBot() {
        onMessage((channel, packagedMessage, timedOut) -> {
            if (!timedOut) {
                channel.receiveMessage(packagedMessage);
            }
        });
        channels = new ConcurrentHashMap<>();
        channelManager = new ChannelManager(channels);
    }

    @Override
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
    public void onMessage(MessageHandler messageHandler) {
        messageHandlers = messageHandlers.andThen((channel, packagedMessage, timedOut) -> {
            // In try/catch block so that one handler can't crash others (unless it Errors)
            try {
                messageHandler.onMessage(channel, packagedMessage, timedOut);
            } catch (Exception e) {
                App.logger.catching(e);
            }
        });
    }

    @Override
    public void invokeMessageHandlers(Channel channel, PackagedMessage message, boolean timedOut) {
        messageHandlers.onMessage(channel, message, timedOut);
    }
}
