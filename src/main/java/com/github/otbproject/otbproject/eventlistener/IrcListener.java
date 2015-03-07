package com.github.otbproject.otbproject.eventlistener;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.Api;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.users.SubscriberStorage;
import com.github.otbproject.otbproject.users.UserLevel;
import com.github.otbproject.otbproject.util.ULUtil;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

/**
 * Created by justin on 29/01/2015.
 */
public class IrcListener extends ListenerAdapter {

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        String channelName = event.getChannel().getName().replace("#", "");
        Channel channel = App.bot.channels.get(channelName);
        String user = event.getUser().getNick();

        String message = event.getMessage();
        if(user.equalsIgnoreCase("jtv")){
            if(message.contains(":SPECIALUSER")) {
                String[] messageSplit = message.split(":SPECIALUSER")[1].split(" ");
                String name = messageSplit[0];
                String userType = messageSplit[1];
                if (userType.equalsIgnoreCase("subscriber")) {
                    channel.subscriberStorage.add(name);
                }
            }
        }else{
            UserLevel userLevel = ULUtil.getUserLevel(channel.getDatabaseWrapper(),channelName,user);
            channel.receiveQueue.add(new PackagedMessage(message, user, channelName, userLevel, MessagePriority.DEFAULT));
        }

    }

    @Override
    public void onJoin(JoinEvent event) {
    }

    @Override
    public void onPart(PartEvent event) {
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        App.logger.info("Disconnected From Twitch");
    }

    @Override
    public void onConnect(ConnectEvent event) {
        App.bot.sendRaw().rawLine("TWITCHCLIENT 3");
    }

}
