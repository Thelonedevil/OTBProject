package com.github.otbproject.otbproject.gui;

import com.github.otbproject.otbproject.bot.Bot;
import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.channel.Channels;
import com.github.otbproject.otbproject.cli.commands.CmdParser;
import com.github.otbproject.otbproject.command.Aliases;
import com.github.otbproject.otbproject.command.Commands;
import com.github.otbproject.otbproject.messages.internal.InternalMessageSender;
import com.github.otbproject.otbproject.util.Util;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiController {

    @FXML
    public TextArea logOutput;
    @FXML
    public TextArea commandsOutput;
    @FXML
    public TextField commandsInput;
    @FXML
    public TextArea cliOutput;

    protected final List<String> history = new ArrayList<>();
    protected int historyPointer = 0;
    private final ExecutorService executorService = Util.getSingleThreadExecutor("CLI Command Processor");
    private List<String> tabCompleteList = Collections.emptyList();
    private int tabCompleteIndex = 0;

    @FXML
    public void command(KeyEvent event) {
        switch (event.getCode()) {
            case ENTER:
                String input = commandsInput.getText();
                if (input.isEmpty()) {
                    break;
                }
                cliOutput.appendText(input + "\n");
                commandsInput.clear();
                commandsInput.setEditable(false);
                commandsInput.setPromptText("Command executing, please wait...");
                executorService.execute(() -> {
                    CmdParser.from(InternalMessageSender.CLI);
                    String output = CmdParser.processLine(input);
                    GuiUtils.runSafe(() -> cliOutput.appendText((output.isEmpty() ? "" : (output + "\n")) + ">  "));
                    GuiApplication.setInputActive();
                });
                if (history.isEmpty() || !history.get(history.size() - 1).equals(input)) {
                    history.add(input);
                }
                while (history.size() > 1000) {
                    history.remove(0);
                }
                historyPointer = history.size();

                notTabCompleting();
                break;
            case UP:
                if (historyPointer == 0) {
                    break;
                }
                --historyPointer;
                commandsInput.setText(history.get(historyPointer));
                commandsInput.positionCaret(commandsInput.getText().length());
                event.consume();

                notTabCompleting();
                break;
            case DOWN:
                if (historyPointer == history.size()) {
                    break;
                } else if (historyPointer == history.size() - 1) {
                    commandsInput.clear();
                    historyPointer = history.size();
                    break;
                }
                historyPointer++;
                commandsInput.setText(history.get(historyPointer));

                notTabCompleting();
                break;
            case TAB:
                input = commandsInput.getText();
                List<String> parts = Stream.of(input.split(" ")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
                if (input.isEmpty() || input.endsWith(" ")) {
                    parts.add("");
                }
                if (parts.size() == 1) {
                    tabComplete(parts, 0, CmdParser.getCommands());
                } else if (parts.size() == 2 && CmdParser.getCommands().contains(parts.get(0))) {
                    switch (parts.get(0)) {
                        case CmdParser.CLEAR:
                            tabComplete(parts, 1, CmdParser.ClearTargets.targets);
                            break;
                        case CmdParser.EXEC:
                        case CmdParser.RESET:
                            tabComplete(parts, 1, Channels.list());
                            break;
                        case CmdParser.LEAVECHANNEL:
                            tabComplete(parts, 1, Channels.list(), s -> !Channels.isBotChannel(s));
                            break;
                        case CmdParser.HELP:
                            tabComplete(parts, 1, CmdParser.getCommands());
                            break;
                    }
                } else if (parts.size() == 3 && parts.get(0).equals(CmdParser.EXEC)) {
                    Optional<Channel> optional = Channels.get(parts.get(1));
                    if (optional.isPresent() && optional.get().isInChannel()) {
                        Channel channel = optional.get();
                        List<String> list = Commands.getCommands(channel.getMainDatabaseWrapper());
                        list = (list == null) ? new ArrayList<>() : list;
                        addIfNotNull(list, Aliases.getAliases(channel.getMainDatabaseWrapper()));
                        if (Channels.isBotChannel(channel)) {
                            addIfNotNull(list, Commands.getCommands(Bot.getBot().getBotDB()));
                            addIfNotNull(list, Aliases.getAliases(Bot.getBot().getBotDB()));
                        }
                        tabComplete(parts, 2, list);
                    }
                } else {
                    notTabCompleting();
                }
                commandsInput.positionCaret(commandsInput.getText().length());
                break;
            case ESCAPE:
                commandsInput.clear();
                historyPointer = history.size();

                notTabCompleting();
                break;
            default:
                notTabCompleting();
        }
    }

    private void tabComplete(List<String> parts, int index, Collection<String> completions) {
        tabComplete(parts, index, completions, s -> true);
    }

    private void tabComplete(List<String> parts, int index, Collection<String> completions, Predicate<String> predicate) {
        if (tabCompleteIndex != 0) {
            multipleTabComplete(parts, index);
            return;
        }

        tabCompleteList = completions.stream()
                .filter(string -> StringUtils.startsWithIgnoreCase(string, parts.get(index)))
                .filter(predicate)
                .sorted()
                .collect(Collectors.toList());
        if (tabCompleteList.size() == 1) {
            commandsInput.setText(getInputPartsTillIndex(parts, index) + tabCompleteList.get(0) + " ");
        } else if (tabCompleteList.size() != 0) {
            multipleTabComplete(parts, index);
        } else {
            notTabCompleting();
        }
    }

    private void multipleTabComplete(List<String> parts, int index) {
        if (tabCompleteIndex >= tabCompleteList.size()) {
            tabCompleteIndex = 0;
        }
        commandsInput.setText(getInputPartsTillIndex(parts, index) + tabCompleteList.get(tabCompleteIndex));
        tabCompleteIndex++;
    }

    private String getInputPartsTillIndex(List<String> parts, int index) {
        List<String> subList = parts.subList(0, ((index > parts.size()) ? parts.size() : index));
        String input = subList.stream().collect(Collectors.joining(" "));
        return input + ((input.length() == 0) ? "" : " ");
    }

    private void notTabCompleting() {
        tabCompleteList.clear();
        tabCompleteIndex = 0;
    }

    private static void addIfNotNull(List<String> l1, List<String> l2) {
        if (l2 != null) {
            l1.addAll(l2);
        }
    }
}
