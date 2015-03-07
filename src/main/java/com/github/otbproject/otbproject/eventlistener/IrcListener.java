package com.github.otbproject.otbproject.eventlistener;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.users.SubscriberStorage;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

/**
 * Created by justin on 29/01/2015.
 */
public class IrcListener extends ListenerAdapter {

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        Channel channel = App.bot.channels.get(event.getChannel().getName().replace("#", ""));

        String message = event.getMessage();
        if(event.getUser().getNick().equalsIgnoreCase("jtv")){
            if(message.contains(":SPECIALUSER")){
                String[] messageSplit = message.split(":SPECIALUSER")[1].split(" ");
                String name = messageSplit[0];
                String userType = messageSplit[1];
                if(userType.equalsIgnoreCase("subscriber")){
                    channel.subscriberStorage.add(name);
                }

            }
        }else{

            channel.receiveQueue.add(new PackagedMessage(event));
        }

    }

    @Override
    public void onJoin(JoinEvent event) {
    }

    @Override
    public void onPart(PartEvent event) {
        //TODO move this to somewhere else (probably be in the CLI leave command executor)
        if (event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick())) {
            App.bot.channels.remove(event.getChannel().getName().replace("#", "")).leave();
        }
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
