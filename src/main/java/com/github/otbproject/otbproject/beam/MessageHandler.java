package com.github.otbproject.otbproject.beam;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.util.ULUtil;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import pro.beam.api.resource.chat.AbstractChatEvent;
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
        App.logger.info("<"+ channelName +"> "+ data.user_name + ": " + getMessage(data));

        // Check if message is from bot and sent by bot
        if (event.data.user_name.equalsIgnoreCase(APIBot.getBot().getUserName()) && ((BeamBot) APIBot.getBot()).sentMessageCache.contains(event.data.getMessage())) {
            App.logger.debug("Ignoring message sent by bot");
            return;
        }

        PackagedMessage packagedMessage = new PackagedMessage(getMessage(data),data.user_name.toLowerCase(), channelName, ULUtil.getUserLevel(APIChannel.get(channelName).getMainDatabaseWrapper(), channelName ,data.user_name.toLowerCase()), MessagePriority.DEFAULT);
        Channel channel = APIChannel.get(channelName);
        if(channel != null){
            channel.receiveQueue.add(packagedMessage);
        }else{
            App.logger.error("Channel: " + channelName + " appears not to exist");
        }
    }

    // Modified version of Beam's default method to join message parts
    // Does not insert a space between different parts like Beam's method does, and gets
    //  plaintext of emoticons
    private static String getMessage(IncomingMessageData data) {
        return Joiner.on("").join(Iterators.transform(data.message.iterator(), part -> {
            switch(part.type) {
                case ME:
                case EMOTICON:
                    return part.text;
                case LINK:
                    return part.url;
                case TEXT:
                default:
                    return part.data;
            }
        }));
    }
}
