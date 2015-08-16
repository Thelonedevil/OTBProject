package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.database.Databases;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractBot implements IBot {
    protected final ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<>();
    protected final DatabaseWrapper botDB = Databases.createBotDbWrapper();
    protected BiConsumer<Channel, PackagedMessage> messageHandlers = (channel, packagedMessage) -> {};

    @Override
    public ConcurrentHashMap<String, Channel> getChannels() {
        return channels;
    }

    @Override
    public DatabaseWrapper getBotDB() {
        return botDB;
    }

    @Override
    public void onMessage(BiConsumer<Channel, PackagedMessage> messageHandler) {
        messageHandlers = messageHandlers.andThen(messageHandler);
    }

    @Override
    public void invokeMessageHandlers(Channel channel, PackagedMessage message) {
        messageHandlers.accept(channel, message);
    }
}
