package com.github.otbproject.otbproject.eventlistener;

import com.github.otbproject.otbproject.App;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by justin on 29/01/2015.
 */
public class IrcListener extends ListenerAdapter {

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        //TODO stuff for messages
    }

    @Override
    public void onJoin(JoinEvent event){
        //TODO join stuff
    }
    @Override
    public void onDisconnect(DisconnectEvent event){
        App.logger.info("Disconnected From Twitch");
    }

}
