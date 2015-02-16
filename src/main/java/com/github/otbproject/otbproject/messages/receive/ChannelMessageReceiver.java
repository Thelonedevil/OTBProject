package com.github.otbproject.otbproject.messages.receive;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.channels.Channel;
import com.github.otbproject.otbproject.config.ChannelConfigHelper;
import com.github.otbproject.otbproject.messages.send.MessageOut;
import com.github.otbproject.otbproject.proc.MessageProcessor;
import com.github.otbproject.otbproject.proc.ProcessedMessage;
import com.github.otbproject.otbproject.proc.ScriptProcessor;
import com.github.otbproject.otbproject.users.UserLevel;
import org.pircbotx.hooks.events.MessageEvent;

public class ChannelMessageReceiver implements Runnable {
    private final Channel channel;
    private MessageReceiveQueue queue;

    public ChannelMessageReceiver(Channel channel, MessageReceiveQueue queue) {
        this.channel = channel;
        this.queue = queue;
    }

    public void run() {
        MessageEvent event;

        try {
            while (true) {
                event = queue.take();
                String channelName = event.getChannel().getName().replace("#","");
                String user = event.getUser().getNick();
                // TODO get actual user level
                UserLevel ul = UserLevel.DEFAULT;
                ProcessedMessage processedMsg = MessageProcessor.process(channel.getDatabaseWrapper(), event.getMessage(), channelName, user, ul, channel.getConfig().isDebug());
                String message = processedMsg.getResponse();
                String commmand = processedMsg.getCommandName();
                if ((processedMsg.isScript() || !message.isEmpty()) && !channel.commandCooldownSet.contains(commmand) && !channel.userCooldownSet.contains(user)) {
                    // Do script (processedMsg.getResponse is the script path)
                    if (processedMsg.isScript()) {
                        boolean success = ScriptProcessor.process(processedMsg.getResponse(), channel.getDatabaseWrapper(), processedMsg.getArgs(), channelName, user, ul);
                        if (!success) {
                            continue;
                        }
                    }
                    // Send message
                    else {
                        MessageOut messageOut = new MessageOut(message);
                        channel.getSendQueue().add(messageOut);
                    }

                    // Handles command cooldowns
                    int commandCooldown = channel.getConfig().getCommandCooldown();
                    if (commandCooldown > 0) {
                        channel.commandCooldownSet.add(commmand, commandCooldown);
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
