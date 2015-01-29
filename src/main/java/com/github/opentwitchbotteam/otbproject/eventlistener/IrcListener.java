package com.github.opentwitchbotteam.otbproject.eventlistener;

import org.pircbotx.hooks.ListenerAdapter;
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
}
