package com.github.otbproject.otbproject.bot.irc;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.AbstractBot;
import com.github.otbproject.otbproject.bot.Bot;
import com.github.otbproject.otbproject.bot.Control;
import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.channel.Channels;
import com.github.otbproject.otbproject.config.Configs;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.proc.TimeoutProcessor;
import com.github.otbproject.otbproject.user.UserLevel;
import com.github.otbproject.otbproject.user.UserLevels;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.pircbotx.output.OutputCAP;

import java.util.Optional;

public class IrcListener extends ListenerAdapter {

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        String channelName = IRCHelper.getInternalChannelName(event.getChannel().getName());
        Optional<Channel> optional = Channels.get(channelName);
        if (!optional.isPresent()) {
            App.logger.error("The channel '" + channelName + "' really shouldn't be null here. Something has gone terribly wrong.");
            return;
        }
        Channel channel = optional.get();

        String user = event.getUser().getNick().toLowerCase();

        String message = event.getMessage();
        TwitchBot bot = (TwitchBot) Control.getBot();
        if(event.getTags().get("subscriber") != null && event.getTags().get("subscriber").equalsIgnoreCase("1")){
            bot.subscriberStorage.put(channelName,user);
        }
        UserLevel userLevel = UserLevels.getUserLevel(channel.getMainDatabaseWrapper(), channelName, user);
        PackagedMessage packagedMessage = new PackagedMessage(message, user, channelName, userLevel, MessagePriority.DEFAULT);
        bot.invokeMessageHandlers(channel, packagedMessage, TimeoutProcessor.doTimeouts(channel, packagedMessage));
    }

    @Override
    public void onJoin(JoinEvent event) {
        if(event.getUser().equals(event.getBot().getUserBot())) {
            ((TwitchBot) Control.getBot()).addJoined(IRCHelper.getInternalChannelName(event.getChannel().getName()),event.getChannel());
        }
    }

    @Override
    public void onPart(PartEvent event) {
        if(event.getUser().equals(event.getBot().getUserBot())) {
            ((TwitchBot) Control.getBot()).removeJoined(IRCHelper.getInternalChannelName(event.getChannel().getName()));
        }
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        App.logger.info("Disconnected From Twitch");
    }

    @Override
    public void onConnect(ConnectEvent event) {
        /*OutputCAP cap = ((TwitchBot) Control.getBot()).getIRC().sendCAP();
        cap.request(":twitch.tv/tags");
        cap.request(":twitch.tv/membership");*/
        // Join bot channel
        Channels.join(Control.getBot().getUserName(), false);
        // Join channels
        Configs.getBotConfig().getCurrentChannels().forEach(channel -> Channels.join(channel, false));
    }

}
