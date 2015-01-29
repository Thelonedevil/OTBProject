package com.github.opentwitchbotteam.otbproject;

import com.github.opentwitchbotteam.otbproject.eventlistener.IrcListener;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;

/**
 * Created by justin on 02/01/2015.
 */
public class App {
    static HashSet<String> channels = new HashSet<>();
    static Listener listener = new IrcListener();
    static PircBotX bot;
    public static void main(String[] args) {
        Configuration.Builder configurationBuilder = new Configuration.Builder().setName("Lone_Bot").setAutoNickChange(false).setCapEnabled(false).addListener(listener).setServerHostname("irc.twitch.tv")
                .setServerPort(6667).setServerPassword("").setEncoding(Charset.forName("UTF-8"));
        channels.forEach(configurationBuilder::addAutoJoinChannel);
        Configuration configuration = configurationBuilder.buildConfiguration();
        bot = new PircBotX(configuration);
        try {
            bot.startBot();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IrcException e) {
            e.printStackTrace();
        }
    }
}
