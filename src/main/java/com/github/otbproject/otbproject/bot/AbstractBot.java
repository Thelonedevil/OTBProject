package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.database.Databases;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.proc.MessageHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

public abstract class AbstractBot implements IBot {
    protected final ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<>();
    protected final DatabaseWrapper botDB = Databases.createBotDbWrapper();
    protected MessageHandler messageHandlers;

    public AbstractBot() {
        messageHandlers = (channel, packagedMessage, timedOut) -> {
            if (!timedOut) {
                channel.receiveMessage(packagedMessage);
            }
        };
    }

    @Override
    public ConcurrentMap<String, Channel> getChannels() {
        return channels;
    }

    @Override
    public DatabaseWrapper getBotDB() {
        return botDB;
    }

    @Override
    public void shutdown() {
        channels.values().forEach(Channel::leave);
    }

    @Override
    public void onMessage(MessageHandler messageHandler) {
        messageHandlers = messageHandlers.andThen(messageHandler);
    }

    @Override
    public void invokeMessageHandlers(Channel channel, PackagedMessage message, boolean timedOut) {
        messageHandlers.onMessage(channel, message, timedOut);
    }
}
