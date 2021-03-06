package com.github.otbproject.otbproject.bot.irc;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.Control;
import com.github.otbproject.otbproject.channel.ChannelManager;
import com.github.otbproject.otbproject.channel.ChannelProxy;
import com.github.otbproject.otbproject.channel.JoinCheck;
import com.github.otbproject.otbproject.config.BotConfig;
import com.github.otbproject.otbproject.config.Configs;
import com.github.otbproject.otbproject.event.ChannelMessageEvent;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.proc.TimeoutProcessor;
import com.github.otbproject.otbproject.user.UserLevel;
import com.github.otbproject.otbproject.user.UserLevels;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

import java.util.EnumSet;
import java.util.Optional;

class IrcListener extends ListenerAdapter {

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        String channelName = IRCHelper.getInternalChannelName(event.getChannel().getName());
        Optional<ChannelProxy> optional = Control.bot().channelManager().get(channelName);
        if (!optional.isPresent()) {
            App.logger.error("The channel '" + channelName + "' really shouldn't be null here. Something has gone terribly wrong.");
            return;
        }
        ChannelProxy channel = optional.get();

        String user = event.getUser().getNick().toLowerCase();

        String message = event.getMessage();
        TwitchBot bot = (TwitchBot) Control.bot();
        if (event.getTags().get("subscriber") != null && event.getTags().get("subscriber").equalsIgnoreCase("1")) {
            bot.subscriberStorage.put(channelName, user);
        }
        UserLevel userLevel = UserLevels.getUserLevel(channel.getMainDatabaseWrapper(), channelName, user);
        PackagedMessage packagedMessage = new PackagedMessage(message, user, channelName, userLevel, MessagePriority.DEFAULT);
        bot.eventBus().post(new ChannelMessageEvent(channel, packagedMessage, TimeoutProcessor.doTimeouts(channel, packagedMessage)));
    }

    @Override
    public void onJoin(JoinEvent event) {
        if (event.getUser().equals(event.getBot().getUserBot())) {
            ((TwitchBot) Control.bot()).addJoined(IRCHelper.getInternalChannelName(event.getChannel().getName()), event.getChannel());
        }
    }

    @Override
    public void onPart(PartEvent event) {
        if (event.getUser().equals(event.getBot().getUserBot())) {
            ((TwitchBot) Control.bot()).removeJoined(IRCHelper.getInternalChannelName(event.getChannel().getName()));
        }
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        App.logger.info("Disconnected From Twitch");
    }

    @Override
    public void onConnect(ConnectEvent event) {
        ChannelManager channelManager = Control.bot().channelManager();
        // Join bot channel
        channelManager.join(Control.bot().getUserName(), EnumSet.of(JoinCheck.WHITELIST, JoinCheck.BLACKLIST));
        // Join channels
        Configs.getBotConfig().get(BotConfig::getCurrentChannels).forEach(channel -> channelManager.join(channel, EnumSet.of(JoinCheck.WHITELIST, JoinCheck.BLACKLIST)));
    }

}
