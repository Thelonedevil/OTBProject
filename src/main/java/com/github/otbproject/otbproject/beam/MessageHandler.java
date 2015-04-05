package com.github.otbproject.otbproject.beam;

import pro.beam.api.resource.chat.events.EventHandler;
import pro.beam.api.resource.chat.events.IncomingMessageEvent;
import pro.beam.api.resource.chat.events.data.IncomingMessageData;

/**
 * Created by Justin on 19/03/2015.
 */
public class MessageHandler implements EventHandler<IncomingMessageEvent> {

    String channelname;
    public MessageHandler(String channel){
        this.channelname = channel;
    }

    @Override
    public void onEvent(IncomingMessageEvent event) {
        IncomingMessageData data = event.data;
        System.out.println("<"+channelname +"> "+ data.user_name + ": " + data.getMessage());
    }
}
