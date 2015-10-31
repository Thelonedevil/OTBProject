package com.github.otbproject.otbproject.command.scheduler;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.Control;
import com.github.otbproject.otbproject.channel.ChannelProxy;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.user.UserLevel;

import java.util.Objects;

public class ScheduledCommand implements Runnable {

    private final ChannelProxy channel;
    private final PackagedMessage packagedMessage;

    public ScheduledCommand(ChannelProxy channel, String command) {
        Objects.requireNonNull(channel, command);
        this.channel = channel;
        packagedMessage = new PackagedMessage(command, Control.getBot().getUserName(), channel.getName(), UserLevel.INTERNAL, MessagePriority.DEFAULT);
    }

    @Override
    public void run() {
        App.logger.debug("Attempting to run scheduled command '" + packagedMessage.message + "' in channel: " + channel.getName());
        channel.receiveMessage(packagedMessage);
    }
}
