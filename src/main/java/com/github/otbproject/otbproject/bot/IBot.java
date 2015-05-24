package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import org.pircbotx.exception.IrcException;

import java.io.IOException;
import java.util.HashMap;

public interface IBot {
    boolean isConnected(String channelName);

    boolean isConnected();

    HashMap<String, Channel> getChannels();

    boolean isChannel(String channelName);

    default void shutdown() {
        getChannels().values().forEach(Channel::leave);
    }

    String getUserName();

    DatabaseWrapper getBotDB();

    boolean isUserMod(String channel, String user);

    void sendMessage(String channel, String message);

    void startBot() throws IOException, IrcException;

    boolean join(String channelName);

    boolean leave(String channelName);

    boolean timeout(String channelName, String user, int timeInSeconds);

    boolean removeTimeout(String channelName, String user);
}
