package com.github.otbproject.otbproject.messages.receive;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.api.APIDatabase;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.config.ChannelConfigHelper;
import com.github.otbproject.otbproject.config.GeneralConfigHelper;
import com.github.otbproject.otbproject.database.DatabaseHelper;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.send.MessageOut;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
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
        try {
            Thread.currentThread().setName(channel.getName() + " Message Receiver");
            PackagedMessage packagedMessage;

            while (true) {
                packagedMessage = queue.take();
                String channelName = channel.getName();
                String user = packagedMessage.getUser();
                boolean inBotChannel = false;
                if (channelName.equals(App.bot.getNick())) {
                    inBotChannel = true;
                }

                Channel destinationChannel = APIChannel.get(packagedMessage.getDestinationChannel());
                if (destinationChannel == null) {
                    continue;
                }

                // Process commands for bot channel
                if (inBotChannel) {
                    DatabaseWrapper db = APIDatabase.getBotDatabase();
                    UserLevel ul = packagedMessage.getUserLevel();
                    ProcessedMessage processedMsg = MessageProcessor.process(db, packagedMessage.getMessage(), channelName, user, ul, true);
                    if (processedMsg.isScript() || !processedMsg.getResponse().isEmpty()) {
                        doResponse(db, processedMsg, channelName, destinationChannel, user, ul, packagedMessage.getMessagePriority(), true);
                        // Don't process response as regular channel if done as bot channel
                        continue;
                    }
                }

                // Pre-check if user is on cooldown
                if (destinationChannel.userCooldownSet.contains(user)) {
                    continue;
                }

                // Process commands not as bot channel
                DatabaseWrapper db = channel.getDatabaseWrapper();
                UserLevel ul = packagedMessage.getUserLevel();
                ProcessedMessage processedMsg = MessageProcessor.process(db, packagedMessage.getMessage(), channelName, user, ul, channel.getConfig().isDebug());

                // Check if bot is enabled
                if (channel.getConfig().isEnabled() || GeneralConfigHelper.isPermanentlyEnabled(App.bot.configManager.getGeneralConfig(), processedMsg.getCommandName())) {
                    if ((processedMsg.isScript() || !processedMsg.getResponse().isEmpty()) && !destinationChannel.commandCooldownSet.contains(processedMsg.getCommandName())) {
                        doResponse(db, processedMsg, channelName, destinationChannel, user, ul, packagedMessage.getMessagePriority(), inBotChannel);
                    }
                }
            }
        } catch (InterruptedException e) {
            App.logger.info("Stopped message receiver for " + channel.getName());
        } catch (Exception e) {
            App.logger.catching(e);
        }
    }

    private void doResponse(DatabaseWrapper db, ProcessedMessage processedMsg, String channelName, Channel destinationChannel, String user, UserLevel ul, MessagePriority priority, boolean inBotChannel) {
        String message = processedMsg.getResponse();
        String command = processedMsg.getCommandName();

        // Do script (processedMsg.getResponse is the script path)
        if (processedMsg.isScript()) {
            boolean success = ScriptProcessor.process(message, db, command, processedMsg.getArgs(), channelName, destinationChannel.getName(), user, ul);
            if (!success) {
                return;
            }
        }
        // Send message
        else {
            MessageOut messageOut = new MessageOut(message, priority);
            // If queue rejects message because it's too full, return
            if (!destinationChannel.sendQueue.add(messageOut)) {
                return;
            }
        }

        // Increment count
        try {
            Command.incrementCount(db, command);
        } catch (SQLException e) {
            App.logger.error("Failed to increment count for command: " + command);
            App.logger.catching(e);
        }

        // Skip cooldowns if bot channel
        if (inBotChannel) {
            return;
        }

        // Handles command cooldowns
        int commandCooldown = channel.getConfig().getCommandCooldown();
        if (commandCooldown > 0) {
            destinationChannel.commandCooldownSet.add(command, commandCooldown);
        }
        // Handles user cooldowns
        int userCooldown = ChannelConfigHelper.getCooldown(channel.getConfig(), ul);
        if (userCooldown > 0) {
            destinationChannel.userCooldownSet.add(user, userCooldown);
        }
    }
}
