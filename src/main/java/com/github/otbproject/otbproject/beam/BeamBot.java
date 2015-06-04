package com.github.otbproject.otbproject.beam;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.api.APIConfig;
import com.github.otbproject.otbproject.api.APIDatabase;
import com.github.otbproject.otbproject.bot.BotUtil;
import com.github.otbproject.otbproject.bot.IBot;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.channels.ChannelInitException;
import com.github.otbproject.otbproject.channels.ChannelNotFoundException;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import net.jodah.expiringmap.ExpiringMap;
import pro.beam.api.BeamAPI;
import pro.beam.api.resource.BeamUser;
import pro.beam.api.resource.chat.methods.ChatSendMethod;
import pro.beam.api.services.impl.UsersService;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BeamBot implements IBot {
    private final HashMap<String, Channel> channels = new HashMap<>();
    private final DatabaseWrapper botDB = APIDatabase.getBotDatabase();

    public final ExpiringMap<String, Boolean> sentMessageCache;
    private static final int CACHE_TIME = 4;

    final BeamAPI beam = new BeamAPI();
    BeamUser beamUser;
    final HashMap<String,BeamChatChannel> beamChannels = new HashMap<>();

    public BeamBot() {
        sentMessageCache = ExpiringMap.builder()
                .expiration(CACHE_TIME, TimeUnit.SECONDS)
                .expirationPolicy(ExpiringMap.ExpirationPolicy.CREATED)
                .build();

        try {
            beamUser = beam.use(UsersService.class).login(APIConfig.getAccount().getName(), APIConfig.getAccount().getPasskey()).get();
        } catch (InterruptedException | ExecutionException e) {
            App.logger.catching(e);
        }
    }

    @Override
    public boolean isConnected(String channelName) {
        return beamChannels.containsKey(channelName);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public HashMap<String, Channel> getChannels() {
        return channels;
    }

    @Override
    public boolean isChannel(String channelName) {
        try {
            for(BeamUser user : beam.use(UsersService.class).search(channelName).get()){
                if (user.username.equalsIgnoreCase(channelName)){
                   return true;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            App.logger.catching(e);
        }
        return false;
    }

    @Override
    public void shutdown() {
        for(String key : beamChannels.keySet()){
            beamChannels.get(key).beamChatConnectable.close();
        }
        beamChannels.clear();
        IBot.super.shutdown();
    }

    @Override
    public String getUserName() {
        return beamUser.username.toLowerCase();
    }

    @Override
    public DatabaseWrapper getBotDB() {
        return botDB;
    }

    @Override
    public boolean isUserMod(String channel, String user) {
        BeamChatChannel beamChatChannel = beamChannels.get(channel);
        if (beamChatChannel == null) {
            return false;
        }

        List<BeamUser.Role> list = beamChatChannel.userRoles.get(user);
        return (list != null) && list.contains(BeamUser.Role.MOD);
    }

    @Override
    public boolean isUserSubscriber(String channel, String user) {
        BeamChatChannel beamChatChannel = beamChannels.get(channel);
        if (beamChatChannel == null) {
            return false;
        }

        List<BeamUser.Role> list = beamChatChannel.userRoles.get(user);
        return (list != null) && list.contains(BeamUser.Role.SUBSCRIBER);
    }

    @Override
    public void sendMessage(String channel, String message) {
        beamChannels.get(channel).beamChatConnectable.send(ChatSendMethod.of(message));
        sentMessageCache.put(message, Boolean.TRUE);
        App.logger.info("Sent: <" + channel + "> " + message);
    }

    @Override
    public void startBot() {
        APIChannel.join(getUserName(), false);
        APIConfig.getBotConfig().currentChannels.forEach(channel -> APIChannel.join(channel, false));
        while(!beamChannels.isEmpty()){
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public boolean join(String channelName) {
        try {
            beamChannels.put(channelName, BeamChatChannel.create(channelName));
        } catch (ChannelInitException ignored) {
            return false;
        }
        return beamChannels.containsKey(channelName);
    }

    @Override
    public boolean leave(String channelName) {
        if (beamChannels.containsKey(channelName)) {
            beamChannels.remove(channelName).beamChatConnectable.close();
        }
        return !beamChannels.containsKey(channelName);
    }

    @Override
    public boolean timeout(String channelName, String user, int timeInSeconds) {
        if (timeInSeconds <= 0) {
            App.logger.warn("Cannot time out user for non-positive amount of time");
            return false;
        }

        user = user.toLowerCase(); // Just in case

        // Check if user has user level mod or higher
        try {
            if (BotUtil.isModOrHigher(channelName, user)) {
                return false;
            }
        } catch (ChannelNotFoundException e) {
            App.logger.error("Channel '" + channelName + "' did not exist in which to timeout user");
            App.logger.catching(e);
        }

        BeamChatChannel beamChatChannel = beamChannels.get(channelName);
        // More null checks!
        if (beamChatChannel == null) {
            App.logger.error("Failed to timeout user: BeamChatChannel for channel '" + channelName + "' is null.");
            return false;
        }


        ExpiringMap<String, Boolean> timeoutSet = beamChatChannel.timeoutSet;
        if (timeoutSet.containsKey(user)) {
            long waitTime = timeoutSet.getExpectedExpiration(user) / 1000;
            // Not perfect because it's based on the original timeout time, not the time left
            //  but there's no way to get the time left
            if (waitTime > timeInSeconds) {
                App.logger.info("Did not timeout user '" + user + "' because they were already timed out for longer than that.");
                return false;
            } else {
                timeoutSet.setExpiration(timeInSeconds, TimeUnit.SECONDS);
                return true;
            }
        }
        timeoutSet.put(user, Boolean.TRUE, timeInSeconds, TimeUnit.SECONDS);
        //beamChatChannel.deleteCachedMessages(user); TODO uncomment when major responsiveness issue is fixed
        App.logger.info("Timed out '" + user + "' in channel '" + channelName + "' for " + timeInSeconds + " seconds");
        return true;
    }

    @Override
    public boolean removeTimeout(String channelName, String user) {
        BeamChatChannel channel = beamChannels.get(channelName);
        if (channel == null) {
            App.logger.error("Failed to remove timeout for user: BeamChatChannel for channel '" + channelName + "' is null.");
            return false;
        }
        channel.timeoutSet.remove(user);
        return true;
    }
}
