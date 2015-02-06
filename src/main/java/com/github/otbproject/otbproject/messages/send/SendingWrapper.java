package com.github.otbproject.otbproject.messages.send;

import com.github.otbproject.otbproject.App;

/**
 * Created by justin on 05/02/2015.
 */
public class SendingWrapper {

    public static void send(String channel, String message){
        App.bot.getUserChannelDao().getChannel("#"+channel).send().message(message);
    }
}
