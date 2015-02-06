package com.github.otbproject.otbproject;

import com.github.otbproject.otbproject.channels.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

import java.util.HashMap;

/**
 * Created by justin on 05/02/2015.
 */
public class CustomBot extends PircBotX {

    public HashMap<String,Channel> channels = new HashMap<>();

    public CustomBot(Configuration<? extends PircBotX> configuration) {
        super(configuration);
    }
}
