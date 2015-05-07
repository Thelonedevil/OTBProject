package com.github.otbproject.otbproject.beam;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.api.APIConfig;
import com.github.otbproject.otbproject.api.APIDatabase;
import com.github.otbproject.otbproject.bot.BotUtil;
import com.github.otbproject.otbproject.bot.IBot;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.channels.ChannelNotFoundException;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.proc.CooldownSet;
import pro.beam.api.BeamAPI;
import pro.beam.api.resource.BeamUser;
import pro.beam.api.resource.chat.methods.ChatSendMethod;
import pro.beam.api.services.impl.UsersService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class BeamBot implements IBot {
    private final HashMap<String, Channel> channels = new HashMap<>();
    private final DatabaseWrapper botDB = APIDatabase.getBotDatabase();

    public final CooldownSet sentMessageCache = new CooldownSet();
    private static final int CACHE_TIME = 4;

    final BeamAPI beam = new BeamAPI();
    BeamUser beamUser;
    final HashMap<String,BeamChatChannel> beamChannels = new HashMap<>();

    public BeamBot() {
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

        String path = BeamAPI.BASE_PATH.resolve("chats/"+beamChatChannel.channel.id+"/users").toString();
        try {
            BeamChatUser[] users = beam.http.get(path,BeamChatUser[].class , new HashMap<>()).get();
            for (BeamChatUser beamChatUser : users) {
                if(beamChatUser.getUser_name().equalsIgnoreCase(user)){
                    return Arrays.asList(beamChatUser.getUser_roles()).contains("Mod");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            App.logger.catching(e);
            return false;
        }
        return false;
    }

    @Override
    public void sendMessage(String channel, String message) {
        beamChannels.get(channel).beamChatConnectable.send(ChatSendMethod.of(message));
        sentMessageCache.add(message, CACHE_TIME);
        App.logger.info("Sent: <" + channel + "> " + message);
    }

    @Override
    public void startBot() {
        APIChannel.join(getUserName(), false);
        for (String channelName : APIConfig.getBotConfig().currentChannels) {
            APIChannel.join(channelName, false);
        }
        while(!beamChannels.isEmpty()){
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public boolean join(String channelName) {
        beamChannels.put(channelName, new BeamChatChannel(channelName));
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


        CooldownSet timeoutSet = beamChatChannel.getTimeoutSet();
        if (timeoutSet.contains(user)) {
            int waitTime = timeoutSet.getCooldownRemover(user).getWaitInSeconds();
            // Not perfect because it's based on the original timeout time, not the time left
            //  but there's no way to get the time left
            if (waitTime > timeInSeconds) {
                App.logger.info("Did not timeout user '" + user + "' because they were already timed out for longer than that.");
                return false;
            } else {
                timeoutSet.remove(user);
            }
        }
        boolean success = timeoutSet.add(user, timeInSeconds);
        if (success) {
            App.logger.info("Timed out '" + user + "' in channel '" + channelName + "' for " + timeInSeconds + " seconds");
        }
        return success;
    }

    @Override
    public boolean removeTimeout(String channelName, String user) {
        BeamChatChannel channel = beamChannels.get(channelName);
        if (channel == null) {
            App.logger.error("Failed to remove timeout for user: BeamChatChannel for channel '" + channelName + "' is null.");
            return false;
        }
        return channel.getTimeoutSet().remove(user);
    }
}
