package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.channel.ChannelManager;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.receive.MessageHandler;

import java.util.concurrent.ConcurrentMap;

public interface Bot {
    boolean isConnected(String channelName);

    boolean isConnected();

    @Deprecated
    ConcurrentMap<String, Channel> getChannels(); // TODO remove

    ChannelManager channelManager();

    boolean isChannel(String channelName);

    void shutdown();

    String getUserName();

    DatabaseWrapper getBotDB();

    boolean isUserMod(String channel, String user);

    boolean isUserSubscriber(String channel, String user);

    void sendMessage(String channel, String message);

    void startBot() throws BotInitException;

    boolean join(String channelName);

    boolean leave(String channelName);

    boolean ban(String channelName, String user);

    boolean unBan(String channelName, String user);

    boolean timeout(String channelName, String user, int timeInSeconds);

    boolean removeTimeout(String channelName, String user);

    void onMessage(MessageHandler messageHandler);

    void invokeMessageHandlers(Channel channel, PackagedMessage message, boolean timedOut);
}
