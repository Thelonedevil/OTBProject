package com.github.otbproject.otbproject.bot.nullbot;

import com.github.otbproject.otbproject.bot.Bot;
import com.github.otbproject.otbproject.bot.BotInitException;
import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.channel.ChannelManager;
import com.github.otbproject.otbproject.channel.ChannelProxy;
import com.github.otbproject.otbproject.channel.ProxiedChannel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.receive.MessageHandler;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;

import java.util.concurrent.ConcurrentMap;

public class NullBot implements Bot {
    private static final ConcurrentMap<String, ProxiedChannel> CHANNELS = new EmptyConcurrentMap<>();
    private static final ChannelManager channelManager = new ChannelManager(CHANNELS);
    private static final DatabaseWrapper DATABASE_WRAPPER = new EmptyDatabaseWrapper();
    public static final NullBot INSTANCE = new NullBot();

    @Deprecated // TODO REMOVE
    private static final ConcurrentMap<String, Channel> DEPRECATED_CHANNELS = new EmptyConcurrentMap<>();

    private NullBot() {
    }

    @Override
    public boolean isConnected(String channelName) {
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public ConcurrentMap<String, Channel> getChannels() {
        return DEPRECATED_CHANNELS;
    }

    @Override
    public ChannelManager channelManager() {
        return channelManager;
    }

    @Override
    public boolean isChannel(String channelName) {
        return false;
    }

    @Override
    public void shutdown() {
        // NO-OP
    }

    @Override
    public String getUserName() {
        return "internal:null-bot";
    }

    @Override
    public DatabaseWrapper getBotDB() {
        return DATABASE_WRAPPER;
    }

    @Override
    public boolean isUserMod(String channel, String user) {
        return false;
    }

    @Override
    public boolean isUserSubscriber(String channel, String user) {
        return false;
    }

    @Override
    public void sendMessage(String channel, String message) {
        // NO-OP
    }

    @Override
    public void startBot() throws BotInitException {
        throw new BotInitException("NullBot cannot be started");
    }

    @Override
    public boolean join(String channelName) {
        return false;
    }

    @Override
    public boolean leave(String channelName) {
        return false;
    }

    @Override
    public boolean ban(String channelName, String user) {
        return false;
    }

    @Override
    public boolean unBan(String channelName, String user) {
        return false;
    }

    @Override
    public boolean timeout(String channelName, String user, int timeInSeconds) {
        return false;
    }

    @Override
    public boolean removeTimeout(String channelName, String user) {
        return false;
    }

    @Override
    public void onMessage(MessageHandler messageHandler) {
        // NO-OP
    }

    @Override
    public void invokeMessageHandlers(ChannelProxy channel, PackagedMessage message, boolean timedOut) {
        // NO-OP
    }
}
