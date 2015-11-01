package com.github.otbproject.otbproject.channel;

public class ProxiedChannel {
    private final Channel channel;
    private final ChannelProxy proxy;

    public ProxiedChannel(Channel channel) {
        this.channel = channel;
        proxy = channel.asProxy();
    }

    public Channel channel() {
        return channel;
    }

    public ChannelProxy proxy() {
        return proxy;
    }
}
