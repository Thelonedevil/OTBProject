package com.github.otbproject.otbproject.beam;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.util.ULUtil;
import pro.beam.api.resource.chat.events.EventHandler;
import pro.beam.api.resource.chat.events.IncomingMessageEvent;
import pro.beam.api.resource.chat.events.data.IncomingMessageData;

public class MessageHandler implements EventHandler<IncomingMessageEvent> {

    String channelName;
    public MessageHandler(String channel){
        this.channelName = channel;
    }

    @Override
    public void onEvent(IncomingMessageEvent event) {
        IncomingMessageData data = event.data;
        App.logger.info("<"+ channelName +"> "+ data.userName + ": " + data.asString());

        // Check if message is from bot and sent by bot
        if (event.data.userName.equalsIgnoreCase(APIBot.getBot().getUserName()) && ((BeamBot) APIBot.getBot()).sentMessageCache.contains(event.data.getMessage())) {
            App.logger.debug("Ignoring message sent by bot");
            return;
        }

        PackagedMessage packagedMessage = new PackagedMessage(data.asString(),data.userName.toLowerCase(), channelName, ULUtil.getUserLevel(APIChannel.get(channelName).getMainDatabaseWrapper(), channelName ,data.userName.toLowerCase()), MessagePriority.DEFAULT);
        Channel channel = APIChannel.get(channelName);
        if(channel != null){
            channel.receiveQueue.add(packagedMessage);
        }else{
            App.logger.error("Channel: " + channelName + " appears not to exist");
        }
    }
}
