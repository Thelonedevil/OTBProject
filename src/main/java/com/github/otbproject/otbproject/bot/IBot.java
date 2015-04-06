package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import org.pircbotx.exception.IrcException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Justin on 05/04/2015.
 */
public interface IBot {
    public boolean isConnected(String channelName);

    public boolean isConnected();

    public HashMap<String, Channel> getChannels();

    public boolean isChannel(String channelName);

    public void shutdown();

    public String getUserName();

    public DatabaseWrapper getBotDB();

    boolean isUserMod(String channel, String user);

    void sendMessage(String channel, String message);

    void startBot() throws IOException, IrcException;

    boolean join(String channelName);

    boolean leave(String channelName);
}
