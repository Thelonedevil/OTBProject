package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.database.Databases;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.receive.MessageHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractBot implements IBot {
    protected final ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<>();
    protected final DatabaseWrapper botDB = Databases.createBotDbWrapper();
    protected MessageHandler messageHandlers = (channel, packagedMessage, timedOut) -> {};

    public AbstractBot() {
        onMessage((channel, packagedMessage, timedOut) -> {
            if (!timedOut) {
                channel.receiveMessage(packagedMessage);
            }
        });
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
