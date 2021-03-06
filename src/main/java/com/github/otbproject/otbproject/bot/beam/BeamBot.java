package com.github.otbproject.otbproject.bot.beam;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.AbstractBot;
import com.github.otbproject.otbproject.bot.BotInitException;
import com.github.otbproject.otbproject.bot.BotUtil;
import com.github.otbproject.otbproject.channel.ChannelInitException;
import com.github.otbproject.otbproject.channel.ChannelNotFoundException;
import com.github.otbproject.otbproject.channel.JoinCheck;
import com.github.otbproject.otbproject.config.Account;
import com.github.otbproject.otbproject.config.BotConfig;
import com.github.otbproject.otbproject.config.Configs;
import com.github.otbproject.otbproject.util.ThreadUtil;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.logging.log4j.Level;
import pro.beam.api.BeamAPI;
import pro.beam.api.resource.BeamUser;
import pro.beam.api.resource.chat.methods.ChatSendMethod;
import pro.beam.api.services.impl.UsersService;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BeamBot extends AbstractBot {
    public final ExpiringMap<String, Boolean> sentMessageCache;
    private static final int CACHE_TIME = 4;

    final BeamAPI beam = new BeamAPI();
    BeamUser beamUser;
    final ConcurrentHashMap<String, BeamChatChannel> beamChannels = new ConcurrentHashMap<>();

    public BeamBot() throws BotInitException {
        super();
        sentMessageCache = ExpiringMap.builder()
                .expiration(CACHE_TIME, TimeUnit.SECONDS)
                .expirationPolicy(ExpiringMap.ExpirationPolicy.CREATED)
                .build();

        try {
            beamUser = beam.use(UsersService.class).login(Configs.getAccount().getExactly(Account::getName), Configs.getAccount().getExactly(Account::getPasskey)).get();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            ThreadUtil.interruptIfInterruptedException(e);
            throw new BotInitException("Unable to connect bot to Beam", e);
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
    public boolean isChannel(String channelName) {
        try {
            return beam.use(UsersService.class).search(channelName).get().stream()
                    .anyMatch(user -> user.username.equalsIgnoreCase(channelName));
        } catch (InterruptedException | ExecutionException e) {
            ThreadUtil.interruptIfInterruptedException(e);
            App.logger.catching(e);
        }
        return false;
    }

    @Override
    public synchronized void shutdown() {
        beamChannels.values().forEach(beamChatChannel -> beamChatChannel.beamChatConnectable.close());
        beamChannels.clear();
        super.shutdown();
    }

    @Override
    public String getUserName() {
        return beamUser.username.toLowerCase();
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
        channelManager().join(getUserName(), EnumSet.of(JoinCheck.WHITELIST, JoinCheck.BLACKLIST));
        Configs.getBotConfig().get(BotConfig::getCurrentChannels).forEach(channel -> channelManager().join(channel, EnumSet.of(JoinCheck.WHITELIST, JoinCheck.BLACKLIST)));
        while (!beamChannels.isEmpty()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
                App.logger.warn("Bot thread interrupted - shutting down bot");
                Thread.currentThread().interrupt();
                shutdown();
                break;
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
        return true;
    }

    @Override
    public boolean leave(String channelName) {
        BeamChatChannel channel = beamChannels.remove(channelName);
        if (channel == null) {
            return false;
        }
        channel.beamChatConnectable.close();
        return true;
    }

    @Override
    public boolean ban(String channelName, String user) {
        return banOrUnBan(channelName, user, true);
    }

    @Override
    public boolean unBan(String channelName, String user) {
        return banOrUnBan(channelName, user, false);
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
            if (TimeUnit.MILLISECONDS.toSeconds(timeoutSet.getExpectedExpiration(user)) > timeInSeconds) {
                App.logger.info("Did not timeout user '" + user + "' because they were already timed out for longer than that.");
                return false;
            } else {
                timeoutSet.setExpiration(timeInSeconds, TimeUnit.SECONDS);
            }
        } else {
            timeoutSet.put(user, Boolean.TRUE, timeInSeconds, TimeUnit.SECONDS);
            beamChatChannel.deleteMessages(user);
        }
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

    public boolean clearChannelCache(String channel) {
        BeamChatChannel beamChatChannel = beamChannels.get(channel);
        if (beamChatChannel == null) {
            return false;
        }
        beamChatChannel.clearCache();
        return true;
    }

    private boolean banOrUnBan(String channelName, String user, boolean ban) {
        String param = ban? "add" : "remove";

        // Check if user has user level mod or higher
        if (ban) {
            try {
                if (BotUtil.isModOrHigher(channelName, user)) {
                    return false;
                }
            } catch (ChannelNotFoundException e) {
                App.logger.error("Unable to get channel '" + channelName + "' to ban user");
                App.logger.catching(e);
            }
        }

        // Get channel info
        BeamChatChannel beamChatChannel = beamChannels.get(channelName);
        if (beamChatChannel == null) {
            return false;
        }

        String path = BeamAPI.BASE_PATH.resolve("channels/" + beamChatChannel.channel.id + "/users/" + user.toLowerCase()).toString();
        HashMap<String, Object> map = new HashMap<>();
        map.put(param, new String[]{"Banned"});
        try {
            Object result = beam.http.patch(path, Object.class, map).get(4, TimeUnit.SECONDS);
            if ((result != null) && result.toString().contains("username")) {
                return true;
            }
        } catch (TimeoutException ignored) {
            App.logger.error("Request to set 'Banned' status of user '" + user + "' timed out");
        } catch (InterruptedException e) {
            App.logger.catching(e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            App.logger.catching(Level.DEBUG, e);
        }
        return false;
    }
}
