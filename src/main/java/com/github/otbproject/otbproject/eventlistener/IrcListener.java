package com.github.otbproject.otbproject.eventlistener;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;

/**
 * Created by justin on 29/01/2015.
 */
public class IrcListener extends ListenerAdapter {

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        Channel channel = App.bot.channels.get(event.getChannel().getName().replace("#",""));
        channel.getReceiveQueue().add(event);
    }

    @Override
    public void onJoin(JoinEvent event){
    }

    @Override
    public void onPart(PartEvent event){
        //TODO move this to somewhere else (probably be in the CLI leave command executor)
        if(event.getUser().getNick().equalsIgnoreCase(event.getBot().getNick())){
            App.bot.channels.remove(event.getChannel().getName().replace("#","")).leave();
        }
    }

    @Override
    public void onDisconnect(DisconnectEvent event){
        App.logger.info("Disconnected From Twitch");
    }

}
