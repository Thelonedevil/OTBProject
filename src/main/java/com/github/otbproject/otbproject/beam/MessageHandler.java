package com.github.otbproject.otbproject.beam;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.util.ULUtil;
import pro.beam.api.resource.chat.events.EventHandler;
import pro.beam.api.resource.chat.events.IncomingMessageEvent;
import pro.beam.api.resource.chat.events.data.IncomingMessageData;

/**
 * Created by Justin on 19/03/2015.
 */
public class MessageHandler implements EventHandler<IncomingMessageEvent> {

    String channelName;
    public MessageHandler(String channel){
        this.channelName = channel;
    }

    @Override
    public void onEvent(IncomingMessageEvent event) {
        IncomingMessageData data = event.data;
        PackagedMessage packagedMessage = new PackagedMessage(data.getMessage(),data.user_name, channelName, ULUtil.getUserLevel(APIChannel.get(channelName).getMainDatabaseWrapper(), channelName,data.user_name), MessagePriority.DEFAULT);
        try {
            APIChannel.get(channelName).receiveQueue.add(packagedMessage);
        } catch (NullPointerException npe) {
            App.logger.catching(npe);
        }
        System.out.println("<"+ channelName +"> "+ data.user_name + ": " + data.getMessage());
    }
}
