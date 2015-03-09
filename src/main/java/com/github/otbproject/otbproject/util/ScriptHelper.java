package com.github.otbproject.otbproject.util;

import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessageOut;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.users.UserLevel;

public class ScriptHelper {
    public static void runCommand(String message, String user, String channel, String destinationChannel, MessagePriority priority) {
        PackagedMessage packagedMessage = new PackagedMessage(message, user, channel, destinationChannel, UserLevel.INTERNAL, priority);
        APIChannel.get(channel).receiveQueue.add(packagedMessage);
    }

    public static void sendMessage(String channel, String message, MessagePriority priority) {
        MessageOut messageOut = new MessageOut(message, priority);
        APIChannel.get(channel).sendQueue.add(messageOut);
    }
}
