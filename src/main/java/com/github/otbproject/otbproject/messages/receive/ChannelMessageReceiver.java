package com.github.otbproject.otbproject.messages.receive;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.config.ChannelConfigHelper;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.send.MessageOut;
import com.github.otbproject.otbproject.proc.MessageProcessor;
import com.github.otbproject.otbproject.proc.ProcessedMessage;
import com.github.otbproject.otbproject.proc.ScriptProcessor;
import com.github.otbproject.otbproject.users.UserLevel;

import java.sql.SQLException;

public class ChannelMessageReceiver implements Runnable {
    private final Channel channel;
    private MessageReceiveQueue queue;

    public ChannelMessageReceiver(Channel channel, MessageReceiveQueue queue) {
        this.channel = channel;
        this.queue = queue;
    }

    public void run() {
        PackagedMessage packagedMessage;

        try {
            while (true) {
                packagedMessage = queue.take();
                String channelName = packagedMessage.getChannel();
                String user = packagedMessage.getUser();
                DatabaseWrapper db = channel.getDatabaseWrapper();
                // TODO get actual user level
                UserLevel ul = UserLevel.DEFAULT;
                ProcessedMessage processedMsg = MessageProcessor.process(db, packagedMessage.getMessage(), channelName, user, ul, channel.getConfig().isDebug());
                String message = processedMsg.getResponse();
                String command = processedMsg.getCommandName();
                if ((processedMsg.isScript() || !message.isEmpty()) && !channel.commandCooldownSet.contains(command) && !channel.userCooldownSet.contains(user)) {
                    // Do script (processedMsg.getResponse is the script path)
                    if (processedMsg.isScript()) {
                        boolean success = ScriptProcessor.process(processedMsg.getResponse(), db, processedMsg.getArgs(), channelName, user, ul);
                        if (!success) {
                            continue;
                        }
                    }
                    // Send message
                    else {
                        MessageOut messageOut = new MessageOut(message, packagedMessage.getMessagePriority());
                        channel.sendQueue.add(messageOut);
                    }

                    // I inncrement count
                    try {
                        Command.incrementCount(db, command);
                    } catch (SQLException e) {
                        App.logger.error("Failed to increment count for command: " + command);
                        App.logger.catching(e);
                    }

                    // Handles command cooldowns
                    int commandCooldown = channel.getConfig().getCommandCooldown();
                    if (commandCooldown > 0) {
                        channel.commandCooldownSet.add(command, commandCooldown);
                    }
                    // Handles user cooldowns
                    int userCooldown = ChannelConfigHelper.getCooldown(channel.getConfig(), ul);
                    if (userCooldown > 0) {
                        channel.userCooldownSet.add(user, userCooldown);
                    }
                }
            }
        } catch (InterruptedException e) {
            // TODO tidy up
            App.logger.info("Stopped message receiver for " + channel.getName());
        }
    }
}
