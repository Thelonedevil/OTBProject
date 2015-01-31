package com.github.opentwitchbotteam.otbproject.messages.send;

public class MessageSender implements Runnable {
    private String channel;

    public MessageSender(String channel) throws NonexistentChannelException {
        this.channel = channel;
    }

    public void run() {
        MessageOut message;

        try {
            while (true) {
                //message = queue.take();
                message = MessageSendQueue.take(channel);
                // TODO send message
                Thread.sleep(2000); // TODO store as constant somewhere
            }
        } catch (InterruptedException e) {
            // TODO log something?
            e.printStackTrace();
            // TODO possibly Thread.currentThread().interrupt();
        } catch (NonexistentChannelException e) {
            // TODO log something
            // This shouldn't happen
            // TODO possibly Thread.currentThread().interrupt();
        }
    }
}
