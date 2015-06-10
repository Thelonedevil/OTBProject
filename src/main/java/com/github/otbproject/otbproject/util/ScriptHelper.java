package com.github.otbproject.otbproject.util;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channels;
import com.github.otbproject.otbproject.messages.internal.InternalMessageSender;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessageOut;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.users.UserLevel;

public class ScriptHelper {
    public static void runCommand(String message, String user, String channel, String destinationChannel, MessagePriority priority) {
        App.logger.debug("Processing message as command: " + message);
        PackagedMessage packagedMessage = new PackagedMessage(message, user, channel, destinationChannel, UserLevel.INTERNAL, priority);
        Channels.get(channel).receiveMessage(packagedMessage);
    }

    public static void sendMessage(String channel, String message, MessagePriority priority) {
        if (channel.startsWith(InternalMessageSender.DESTINATION_PREFIX)) {
            InternalMessageSender.send(channel.replace(InternalMessageSender.DESTINATION_PREFIX, ""), message, "CmdExec");
        } else {
            MessageOut messageOut = new MessageOut(message, priority);
            Channels.get(channel).sendMessage(messageOut);
        }
    }
}
