package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public interface IBot {
    boolean isConnected(String channelName);

    boolean isConnected();

    ConcurrentHashMap<String, Channel> getChannels();

    boolean isChannel(String channelName);

    default void shutdown() {
        getChannels().values().forEach(Channel::leave);
    }

    String getUserName();

    DatabaseWrapper getBotDB();

    boolean isUserMod(String channel, String user);

    boolean isUserSubscriber(String channel, String user);

    void sendMessage(String channel, String message);

    void startBot() throws BotInitException;

    boolean join(String channelName);

    boolean leave(String channelName);

    boolean ban(String channelName, String user);

    boolean timeout(String channelName, String user, int timeInSeconds);

    boolean removeTimeout(String channelName, String user);

    void onMessage(BiConsumer<Channel, PackagedMessage> messageHandler);

    void invokeMessageHandlers(Channel channel, PackagedMessage message);
}
