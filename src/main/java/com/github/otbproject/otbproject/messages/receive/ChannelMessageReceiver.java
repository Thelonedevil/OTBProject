package com.github.otbproject.otbproject.messages.receive;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.api.APIChannel;
import com.github.otbproject.otbproject.api.APIConfig;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.commands.Command;
import com.github.otbproject.otbproject.config.ChannelConfigHelper;
import com.github.otbproject.otbproject.config.GeneralConfigHelper;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.internal.InternalMessageSender;
import com.github.otbproject.otbproject.messages.send.MessageOut;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.proc.MessageProcessor;
import com.github.otbproject.otbproject.proc.ProcessedMessage;
import com.github.otbproject.otbproject.proc.CommandScriptProcessor;
import com.github.otbproject.otbproject.users.UserLevel;

public class ChannelMessageReceiver implements Runnable {
    private final Channel channel;
    private final MessageReceiveQueue queue;
    private final boolean inBotChannel;

    public ChannelMessageReceiver(Channel channel, MessageReceiveQueue queue) {
        this.channel = channel;
        this.queue = queue;
        inBotChannel = this.channel.getName().equals(APIBot.getBot().getUserName());
    }

    public void run() {
        try {
            Thread.currentThread().setName(channel.getName() + " Message Receiver");
            PackagedMessage packagedMessage;

            while (true) {
                packagedMessage = queue.take();
                processMessage(packagedMessage);
            }
        } catch (InterruptedException e) {
            App.logger.info("Stopped message receiver for " + channel.getName());
        } catch (Exception e) {
            App.logger.catching(e);
        }
    }

    public void processMessage(PackagedMessage packagedMessage) {
        boolean internal;
        String channelName = channel.getName();
        String user = packagedMessage.getUser();

        String destChannelName = packagedMessage.getDestinationChannel();
        Channel destChannel = null;
        if (packagedMessage.getDestinationChannel().startsWith(InternalMessageSender.DESTINATION_PREFIX)) {
            internal = true;
        } else {
            internal = false;
            destChannel = APIChannel.get(packagedMessage.getDestinationChannel());
            if (destChannel == null || !APIChannel.in(destChannelName)) {
                App.logger.warn("Attempted to process message to be sent in channel in which bot is not listening: " + destChannelName);
                return;
            }
        }

        // Process commands for bot channel
        if (inBotChannel) {
            DatabaseWrapper db = APIBot.getBot().getBotDB();
            UserLevel ul = packagedMessage.getUserLevel();
            ProcessedMessage processedMsg = MessageProcessor.process(db, packagedMessage.getMessage(), channelName, user, ul, APIConfig.getBotConfig().isBotChannelDebug());
            if (processedMsg.isScript() || !processedMsg.getResponse().isEmpty()) {
                doResponse(db, processedMsg, channelName, destChannelName, destChannel, user, ul, packagedMessage.getMessagePriority(), internal);
                // Don't process response as regular channel if done as bot channel
                return;
            }
        }

        // Pre-check if user is on cooldown (skip if internal)
        if (!internal && destChannel.isUserCooldown(user)) {
            return;
        }

        // Process commands not as bot channel
        DatabaseWrapper db = channel.getMainDatabaseWrapper();
        UserLevel ul = packagedMessage.getUserLevel();
        boolean debug = channel.getConfig().isDebug();
        if (inBotChannel) {
            debug = (debug || APIConfig.getBotConfig().isBotChannelDebug());
        }
        ProcessedMessage processedMsg = MessageProcessor.process(db, packagedMessage.getMessage(), channelName, user, ul, debug);

        // Check if bot is enabled
        if (channel.getConfig().isEnabled() || GeneralConfigHelper.isPermanentlyEnabled(APIConfig.getGeneralConfig(), processedMsg.getCommandName())) {
            // Check if empty message, and then if command is on cooldown (skip cooldown check if internal)
            if ((processedMsg.isScript() || !processedMsg.getResponse().isEmpty()) && (internal || !destChannel.isCommandCooldown(processedMsg.getCommandName()))) {
                doResponse(db, processedMsg, channelName, destChannelName, destChannel, user, ul, packagedMessage.getMessagePriority(), internal);
            }
        }
    }

    private void doResponse(DatabaseWrapper db, ProcessedMessage processedMsg, String channelName, String destinationChannelName, Channel destinationChannel, String user, UserLevel ul, MessagePriority priority, boolean internal) {
        String message = processedMsg.getResponse();
        String command = processedMsg.getCommandName();

        // Do script (processedMsg.getResponse is the script path)
        if (processedMsg.isScript()) {
            boolean success = CommandScriptProcessor.process(message, db, command, processedMsg.getArgs(), channelName, destinationChannelName, user, ul);
            if (!success) {
                return;
            }
        }
        // Send message
        else {
            MessageOut messageOut = new MessageOut(message, priority);
            if (internal) {
                InternalMessageSender.send(destinationChannelName.replace(InternalMessageSender.DESTINATION_PREFIX, ""), messageOut.getMessage(), "CmdExec");
                return;
            }
            // If queue rejects message because it's too full, return
            else if (!destinationChannel.sendMessage(messageOut)) {
                return;
            }
        }

        // Increment count
        Command.incrementCount(db, command);

        // Skip cooldowns if bot channel or internal
        if (inBotChannel || (destinationChannelName.equals(APIBot.getBot().getUserName())) || internal) {
            return;
        }

        // Handles command cooldowns
        int commandCooldown = channel.getConfig().getCommandCooldown();
        if (commandCooldown > 0) {
            destinationChannel.addCommandCooldown(command, commandCooldown);
        }
        // Handles user cooldowns
        int userCooldown = ChannelConfigHelper.getCooldown(channel.getConfig(), ul);
        if (userCooldown > 0) {
            destinationChannel.addUserCooldown(user, userCooldown);
        }
    }
}
