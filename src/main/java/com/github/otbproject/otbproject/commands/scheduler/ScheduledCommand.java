package com.github.otbproject.otbproject.commands.scheduler;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.users.UserLevel;

public class ScheduledCommand implements Runnable {

    private final String channel;
    private final PackagedMessage packagedMessage;

    public ScheduledCommand(String channel, String command){
        this.channel = channel;
        packagedMessage = new PackagedMessage(command, APIBot.getBot().getUserName(), channel, channel, UserLevel.INTERNAL, MessagePriority.DEFAULT);
    }

    @Override
    public void run() {
        try {
            APIChannel.get(channel).receiveMessage(packagedMessage);
        } catch (NullPointerException npe) {
            App.logger.catching(npe);
        }
    }
}
