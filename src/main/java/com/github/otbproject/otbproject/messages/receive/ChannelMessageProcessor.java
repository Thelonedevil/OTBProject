package com.github.otbproject.otbproject.messages.receive;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.Control;
import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.channel.Channels;
import com.github.otbproject.otbproject.command.Commands;
import com.github.otbproject.otbproject.config.*;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.messages.internal.InternalMessageSender;
import com.github.otbproject.otbproject.messages.send.MessageOut;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.proc.*;
import com.github.otbproject.otbproject.user.UserLevel;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChannelMessageProcessor {
    private final Channel channel;
    private final String channelName;
    private final boolean inBotChannel;
    private final Lock lock = new ReentrantLock();

    public ChannelMessageProcessor(Channel channel) {
        this.channel = channel;
        channelName = channel.getName();
        inBotChannel = this.channel.getName().equals(Control.getBot().getUserName());
    }

    public void process(PackagedMessage packagedMessage) {
        boolean internal;
        String user = packagedMessage.user;

        String destChannelName = packagedMessage.destinationChannel;
        Channel destChannel = null;
        if (packagedMessage.destinationChannel.startsWith(InternalMessageSender.DESTINATION_PREFIX)) {
            internal = true;
        } else {
            internal = false;
            Optional<Channel> optional = Channels.get(packagedMessage.destinationChannel);
            if (!optional.isPresent() || !optional.get().isInChannel()) {
                App.logger.warn("Attempted to process message to be sent in channel in which bot is not listening: " + destChannelName);
                return;
            }
            destChannel = optional.get();
        }

        // Process commands for bot channel
        if (inBotChannel) {
            DatabaseWrapper db = Control.getBot().getBotDB();
            UserLevel ul = packagedMessage.userLevel;
            ProcessedCommand processedCmd = CommandProcessor.process(db, packagedMessage.message, channelName, user, ul, Configs.getFromBotConfig(BotConfig::isBotChannelDebug));
            if (processedCmd.isScript || !processedCmd.response.isEmpty()) {
                doResponse(db, processedCmd, channelName, destChannelName, destChannel, user, ul, packagedMessage.messagePriority, internal);
                // Don't process response as regular channel if done as bot channel
                return;
            }
        }

        // Process commands not as bot channel
        DatabaseWrapper db = channel.getMainDatabaseWrapper();
        UserLevel ul = packagedMessage.userLevel;
        boolean debug = channel.getFromConfig(ChannelConfig::isDebug);
        if (inBotChannel) {
            debug = (debug || Configs.getFromBotConfig(BotConfig::isBotChannelDebug));
        }
        ProcessedCommand processedCmd = CommandProcessor.process(db, packagedMessage.message, channelName, user, ul, debug);

        // Check if bot is enabled or command is permanently enabled
        if (channel.getFromConfig(ChannelConfig::isEnabled)
                || Configs.getFromGeneralConfig(GeneralConfig::getPermanentlyEnabledCommands).contains(processedCmd.commandName)) {
            // Check if empty message
            if (processedCmd.isScript || !processedCmd.response.isEmpty()) {
                // Check if command is on cooldown (skip cooldown check if internal)
                if (!internal && destChannel.isCommandCooldown(processedCmd.commandName)) {
                    App.logger.debug("Skipping command on cooldown: " + processedCmd.commandName);
                } else if (!internal && destChannel.isUserCooldown(user)) {
                    App.logger.debug("Skipping user on cooldown: " + user);
                } else {
                    doResponse(db, processedCmd, channelName, destChannelName, destChannel, user, ul, packagedMessage.messagePriority, internal);
                }
            }
        }
    }

    private void doResponse(DatabaseWrapper db, ProcessedCommand processedCmd, String channelName, String destChannelName, Channel destChannel, String user, UserLevel ul, MessagePriority priority, boolean internal) {
        String message = processedCmd.response;
        String command = processedCmd.commandName;

        // Do script (processedMsg.response is the script path)
        // There is a slight chance that a cooldown will have been set for the script command since the method was called,
        //  and that it will be run even though it's not supposed to, but processing a script takes too long to lock
        boolean success;
        boolean doIncrement;

        if (processedCmd.isScript) {
            success = CommandScriptProcessor.process(message, db, command, processedCmd.args, channelName, destChannelName, user, ul);
            lock.lock();
            try {
                doIncrement = postResponse(destChannelName, destChannel, command, user, ul, internal, success);
            } finally {
                lock.unlock();
            }
        }
        // Send message
        else {
            if (internal) {
                InternalMessageSender.send(destChannelName.substring(InternalMessageSender.DESTINATION_PREFIX.length()), message, "CmdExec");
                return;
            }
            lock.lock();
            try {
                success = destChannel.sendMessage(new MessageOut(message, priority));
                doIncrement = postResponse(destChannelName, destChannel, command, user, ul, false, success);
            } finally {
                lock.unlock();
            }
        }

        // Increment count (not essential to lock)
        if (doIncrement) {
            Commands.incrementCount(db, command);
        }
    }

    private boolean postResponse(String destChannelName, Channel destChannel, String command, String user, UserLevel ul, boolean internal, boolean success) {
        if (!success) {
            return false;
        }

        // Skip cooldowns if in or sending to bot channel, or internal
        if (inBotChannel || destChannelName.equals(Control.getBot().getUserName()) || internal) {
            return false;
        }

        // Handles command cooldowns
        int commandCooldown = channel.getFromConfig(ChannelConfig::getCommandCooldown);
        if (commandCooldown > 0) {
            destChannel.addCommandCooldown(command, commandCooldown);
        }
        // Handles user cooldowns
        int userCooldown = ChannelConfigHelper.getCooldown(channel, ul);
        if (userCooldown > 0) {
            destChannel.addUserCooldown(user, userCooldown);
        }

        return true;
    }
}
