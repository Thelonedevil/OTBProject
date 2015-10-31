package com.github.otbproject.otbproject.bot.irc;

import com.github.otbproject.otbproject.App;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;
import org.pircbotx.InputParser;
import org.pircbotx.UserHostmask;
import org.pircbotx.Utils;
import org.pircbotx.cap.CapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.events.ServerPingEvent;
import org.pircbotx.hooks.events.UnknownEvent;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

class InputParserImproved extends InputParser {

    public InputParserImproved(IRCBot bot) {
        super(bot);
    }

    @Override
    public void handleLine(String rawLine) throws IOException, IrcException {
        if (rawLine == null) {
            throw new NullPointerException("rawLine");
        } else {
            String line = CharMatcher.WHITESPACE.trimFrom(rawLine);
            com.google.common.collect.ImmutableMap.Builder<String, String> tags = ImmutableMap.builder();
            String command;
            if (line.startsWith("@")) {
                String parsedLine = line.substring(1, line.indexOf(" "));
                line = line.substring(line.indexOf(" ") + 1);
                StringTokenizer sourceRaw = new StringTokenizer(parsedLine);

                while (sourceRaw.hasMoreTokens()) {
                    command = sourceRaw.nextToken(";");
                    if (command.contains("=")) {
                        String[] target = command.split("=");
                        tags.put(target[0], target.length == 2 ? target[1] : "");
                    } else {
                        tags.put(command, "");
                    }
                }
            }

            List<String> parsedLine1 = Utils.tokenizeLine(line);
            String sourceRaw1 = "";
            if (parsedLine1.get(0).charAt(0) == 58) {
                sourceRaw1 = parsedLine1.remove(0);
            }

            command = parsedLine1.remove(0).toUpperCase(this.configuration.getLocale());
            if (!command.equals("PING")) {
                App.logger.info(rawLine);
            }
            if (command.equals("PING")) {
                this.configuration.getListenerManager().onEvent(new ServerPingEvent(this.bot, parsedLine1.get(0)));
            } else if (command.startsWith("ERROR")) {
                this.bot.close();
            } else {
                String target1 = parsedLine1.isEmpty() ? "" : parsedLine1.get(0);
                if (target1.startsWith(":")) {
                    target1 = target1.substring(1);
                }

                if (sourceRaw1.startsWith(":")) {
                    if (((IRCBot) this.bot).notLoggedIn()) {
                        this.processConnect(line, command, target1, parsedLine1);
                    }

                    int code1 = Utils.tryParseInt(command, -1);
                    if (code1 != -1) {
                        this.processServerResponse(code1, line, parsedLine1);
                    } else {
                        UserHostmask source1 = this.bot.getConfiguration().getBotFactory().createUserHostmask(this.bot, sourceRaw1.substring(1));
                        this.processCommand(target1, source1, command, line, parsedLine1, tags.build());
                    }
                } else {
                    this.configuration.getListenerManager().onEvent(new UnknownEvent(this.bot, line));
                    if (((IRCBot) this.bot).notLoggedIn()) {
                        for (CapHandler source : this.configuration.getCapHandlers()) {
                            if (source.handleUnknown(this.bot, line)) {
                                this.addCapHandlerFinished(source);
                            }
                        }
                    }

                }
            }
        }
    }

}
