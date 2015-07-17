package com.github.otbproject.otbproject.bot.irc;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.AbstractBot;
import com.github.otbproject.otbproject.bot.BotInitException;
import com.github.otbproject.otbproject.bot.BotUtil;
import com.github.otbproject.otbproject.channel.ChannelNotFoundException;
import com.github.otbproject.otbproject.serviceapi.ApiRequest;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.isomorphism.util.TokenBucket;
import org.isomorphism.util.TokenBuckets;
import org.pircbotx.exception.IrcException;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TwitchBot extends AbstractBot {
    private IRCBot ircBot = new IRCBot();

    // Should take slightly more than 30 seconds to refill 99 tokens adding 1
    // token every 304 milliseconds
    private final TokenBucket tokenBucket = TokenBuckets.builder().withCapacity(99).withFixedIntervalRefillStrategy(1, 304, TimeUnit.MILLISECONDS).build();

    public final SetMultimap<String, String> subscriberStorage = Multimaps.newSetMultimap(new ConcurrentHashMap<>(), ConcurrentHashMap::newKeySet);

    @Override
    public boolean isConnected(String channelName) {
        return ircBot.getUserChannelDao().getAllChannels().contains(ircBot.getUserChannelDao().getChannel(channelName));
    }

    @Override
    public boolean isConnected() {
        return ircBot.isConnected();
    }

    @Override
    public boolean isChannel(String channelName) {
        return ApiRequest.attemptRequest("channels/" + channelName, 3, 500) != null;
    }

    @Override
    public void shutdown() {
        ircBot.shutdown();
        super.shutdown();
    }

    @Override
    public String getUserName() {
        return ircBot.getNick();
    }

    @Override
    public boolean isUserMod(String channel, String user) {
        return ircBot.getUserChannelDao().getChannel(IRCHelper.getIrcChannelName(channel)).isOp(ircBot.getUserChannelDao().getUser(user));
    }

    @Override
    public boolean isUserSubscriber(String channel, String user) {
        return subscriberStorage.remove(channel, user);
    }

    @Override
    public void sendMessage(String channel, String message) {
        tokenBucket.consume();
        ircBot.getUserChannelDao().getChannel(IRCHelper.getIrcChannelName(channel)).send().message(message);
    }

    @Override
    public void startBot() throws BotInitException {
        try {
            ircBot.startBot();
        } catch (IOException | IrcException e) {
            throw new BotInitException("Failed to start bot", e);
        }
    }

    @Override
    public boolean join(String channelName) {
        tokenBucket.consume();
        ircBot.sendIRC().joinChannel(IRCHelper.getIrcChannelName(channelName));
        return ircBot.getUserChannelDao().getAllChannels().contains(ircBot.getUserChannelDao().getChannel(channelName));
    }

    @Override
    public boolean leave(String channelName) {
        tokenBucket.consume();
        ircBot.getUserChannelDao().getChannel(IRCHelper.getIrcChannelName(channelName)).send().part();
        return !ircBot.getUserChannelDao().getAllChannels().contains(ircBot.getUserChannelDao().getChannel(channelName));
    }

    @Override
    public boolean ban(String channelName, String user) {
        // Check if user has user level mod or higher
        try {
            if (BotUtil.isModOrHigher(channelName, user)) {
                return false;
            }
        } catch (ChannelNotFoundException e) {
            App.logger.error("Channel '" + channelName + "' did not exist in which to timeout user");
            App.logger.catching(e);
        }

        sendMessage(channelName, ".ban " + user);
        return true;
    }

    @Override
    public boolean timeout(String channelName, String user, int timeInSeconds) {
        if (timeInSeconds <= 0) {
            App.logger.warn("Cannot time out user for non-positive amount of time");
            return false;
        }

        // Check if user has user level mod or higher
        try {
            if (BotUtil.isModOrHigher(channelName, user)) {
                return false;
            }
        } catch (ChannelNotFoundException e) {
            App.logger.error("Channel '" + channelName + "' did not exist in which to timeout user");
            App.logger.catching(e);
        }

        sendMessage(channelName, ".timeout " + user + " " + timeInSeconds);
        return true;
    }

    @Override
    public boolean removeTimeout(String channelName, String user) {
        sendMessage(channelName, ".unban " + user); // TODO deal with danger of unbanning someone who's banned
        return true;
    }
}
