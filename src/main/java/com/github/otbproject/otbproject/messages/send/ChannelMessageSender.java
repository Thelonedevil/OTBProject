package com.github.otbproject.otbproject.messages.send;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.api.APIConfig;
import com.github.otbproject.otbproject.channels.Channel;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// This class is not in general thread-safe. Thread safety should be enforced by the
// class using this one
public class ChannelMessageSender implements Runnable {
    private static final ExecutorService EXECUTOR_SERVICE;

    private final Channel channel;
    private final MessageSendQueue queue;
    private Future<?> future;
    boolean active = false;

    static {
        EXECUTOR_SERVICE = Executors.newCachedThreadPool(
                new ThreadFactoryBuilder()
                        .setUncaughtExceptionHandler((t, e) -> {
                            App.logger.error("Thread crashed: " + t.getName());
                            App.logger.catching(e);
                        })
                        .build()
        );
    }

    public ChannelMessageSender(Channel channel) {
        this.channel = channel;
        this.queue = new MessageSendQueue(channel);
    }

    public boolean start() {
        if (active) {
            return false;
        }
        active = true;
        future = EXECUTOR_SERVICE.submit(this);
        return true;
    }

    public boolean stop() {
        if (!active) {
            return false;
        }
        active = false;
        future.cancel(true);
        queue.clear();
        return true;
    }

    // Queues message even if sender is not active.
    // Queue itself is thread-safe, however
    public boolean send(MessageOut messageOut) {
        return queue.add(messageOut);
    }

    public void run() {
        try {
            Thread.currentThread().setName(channel.getName() + " Message Sender");
            MessageOut message;
            int sleepTime = APIConfig.getBotConfig().getMessageSendDelayInMilliseconds();

            while (true) {
                message = queue.take();
                APIBot.getBot().sendMessage(channel.getName(), message.getMessage());
                Thread.sleep(sleepTime);
            }
        } catch (InterruptedException e) {
            App.logger.info("Stopped message sender for " + channel.getName());
        } catch (Exception e) {
            App.logger.catching(e);
        }
    }
}
