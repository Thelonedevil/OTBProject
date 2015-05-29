package com.github.otbproject.otbproject.beam;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIBot;
import com.github.otbproject.otbproject.proc.CooldownSet;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import pro.beam.api.resource.BeamUser;
import pro.beam.api.resource.channel.BeamChannel;
import pro.beam.api.resource.chat.BeamChat;
import pro.beam.api.resource.chat.BeamChatConnectable;
import pro.beam.api.resource.chat.events.IncomingMessageEvent;
import pro.beam.api.resource.chat.events.data.IncomingMessageData;
import pro.beam.api.resource.chat.methods.AuthenticateMessage;
import pro.beam.api.services.impl.ChatService;
import pro.beam.api.services.impl.UsersService;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BeamChatChannel {
    final BeamBot beamBot;
    BeamChat beamChat;
    final BeamChatConnectable beamChatConnectable;
    BeamChannel channel;
    public final CooldownSet<String> timeoutSet = new CooldownSet<>();

    private static final int CACHE_EXPIRATION_MIN = 5;
    private static final int CACHE_MAX_SIZE = 200;
    public final ConcurrentHashMap<String, Set<String>> cacheLookup = new ConcurrentHashMap<>();
    public final Cache<String, IncomingMessageData> messageCache;


    public BeamChatChannel(String channelName){
        // Initialize cache
        messageCache = CacheBuilder.newBuilder()
                .expireAfterWrite(CACHE_EXPIRATION_MIN, TimeUnit.MINUTES)
                .maximumSize(CACHE_MAX_SIZE)
                .removalListener(new RemovalListener<String, IncomingMessageData>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, IncomingMessageData> notification) {
                        IncomingMessageData data = notification.getValue();
                        if (data != null) {
                            removeFromCacheLookup(notification.getValue().user_name, notification.getKey());
                        }
                    }
                })
                .build();

        beamBot = ((BeamBot) APIBot.getBot());
        try {
            channel = beamBot.beamUser.channel;
            for(BeamUser user : beamBot.beam.use(UsersService.class).search(channelName).get()){
                if (user.username.equalsIgnoreCase(channelName)){
                     channel = beamBot.beam.use(UsersService.class).findOne(user.id).get().channel;
                    break;
                }
            }
            beamChat = beamBot.beam.use(ChatService.class).findOne(channel.id).get();
        } catch (InterruptedException | ExecutionException e) {
            App.logger.catching(e);
        }
        beamChatConnectable = beamChat.makeConnectable(beamBot.beam);
        boolean connected = false;
        try {
            connected = beamChatConnectable.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (connected) {
            beamChatConnectable.send(AuthenticateMessage.from(channel, beamBot.beamUser, beamChat.authkey));
            try {
                Thread.sleep(200);// needed to allow the authentication
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            beamChatConnectable.on(IncomingMessageEvent.class, new MessageHandler(channelName));
            App.logger.info("Connected to: " + channelName);

        }
    }

    // TODO maybe lock
    private void addToCacheLookup(String user, String messageId) {
        Set<String> set = cacheLookup.get(user);
        if (set != null) {
            set.add(messageId);
        } else {
            Set<String> newSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
            newSet.add(messageId);
            set = cacheLookup.put(user, newSet);
            if (set != null) {
                newSet.addAll(set);
            }
        }
    }

    // TODO maybe lock
    private boolean removeFromCacheLookup(String user, String messageId) {
        Set<String> set = cacheLookup.get(user);
        if (set != null) {
            boolean success = set.remove(messageId);
            if (set.isEmpty()) {
                cacheLookup.remove(user);
            }
            return success;
        }
        return false;
    }

    public void cacheMessage(IncomingMessageData data) {
        if (data == null) {
            return;
        }
        addToCacheLookup(data.user_name, data.id);
        messageCache.put(data.id, data);
    }

    public void clearCache() {
        messageCache.invalidateAll();
        cacheLookup.clear();
    }

    public void invalidateAll(String user) {
        Set<String> set = cacheLookup.get(user);
        if (set == null) {
            return;
        }
        set.forEach(messageCache::invalidate);
    }
}
