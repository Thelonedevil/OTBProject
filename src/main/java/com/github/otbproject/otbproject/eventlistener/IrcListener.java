package com.github.otbproject.otbproject.eventlistener;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.messages.send.MessageOut;
import com.github.otbproject.otbproject.messages.send.MessageSendQueue;
import com.github.otbproject.otbproject.proc.MessageProcessor;
import com.github.otbproject.otbproject.proc.ProcessedMessage;
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
        //TODO replace booleans with lookups
        ProcessedMessage processedMessage = MessageProcessor.process(App.bot.channels.get(event.getChannel().getName()).getDatabaseWrapper(),event.getMessage(),event.getChannel().getName(),event.getUser().getNick(),false, false);
            String message = processedMessage.getResponse();
            if (message.isEmpty()) {
                MessageOut messageOut = new MessageOut(message);
                MessageSendQueue.add(event.getChannel().getName(), messageOut);
            }
    }

    @Override
    public void onJoin(JoinEvent event){
        Channel channel = new Channel(event.getChannel().getName());
        channel.join();
        App.bot.channels.put(channel.getName(),channel);
    }

    @Override
    public void onPart(PartEvent event){
        App.bot.channels.get(event.getChannel().getName()).leave();
        App.bot.channels.remove(event.getChannel().getName());
    }

    @Override
    public void onDisconnect(DisconnectEvent event){
        App.logger.info("Disconnected From Twitch");
    }

}
