package com.github.otbproject.otbproject;

import com.github.otbproject.otbproject.eventlistener.IrcListener;
import com.github.otbproject.otbproject.fs.FSUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    public static HashSet<String> channels = new HashSet<>();
    static Listener listener = new IrcListener();
    public static PircBotX bot;
    public static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        System.setProperty("OTBCONF", FSUtil.logsDir());
        Configuration.Builder configurationBuilder = new Configuration.Builder().setName("Lone_Bot").setAutoNickChange(false).setCapEnabled(false).addListener(listener).setServerHostname("irc.twitch.tv")
                .setServerPort(6667).setServerPassword("").setEncoding(Charset.forName("UTF-8"));
        channels.forEach(configurationBuilder::addAutoJoinChannel);
        Configuration configuration = configurationBuilder.buildConfiguration();
        logger.info("Bot configuration built");
        bot = new PircBotX(configuration);
        try {
            logger.info("Bot Started");
            bot.startBot();
        } catch (IOException e) {
            logger.catching(e);
        } catch (IrcException e) {
            logger.catching(e);
        }
    }
}
