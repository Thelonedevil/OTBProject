package com.github.otbproject.otbproject.gui;

import com.github.otbproject.otbproject.bot.Bot;
import com.github.otbproject.otbproject.channel.Channels;
import com.github.otbproject.otbproject.cli.commands.CmdParser;
import com.github.otbproject.otbproject.messages.internal.InternalMessageSender;
import com.github.otbproject.otbproject.util.Util;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
                if (input.isEmpty()) {
                    break;
                }
                String[] parts = input.split(" ");
                if (parts.length == 1) {
                    tabComplete(parts, 0, CmdParser.getCommands());

                    //CmdParser.getCommands().forEach(s -> commandsInput.setText(s.startsWith(parts[0]) ? (s + " ") : commandsInput.getText()));
                }
                if (parts.length == 2 && CmdParser.getCommands().contains(parts[0])) {
                    switch (parts[0]) {
                        case CmdParser.CLEAR:
                            tabComplete(parts, 1, CmdParser.ClearTargets.targets);
                            //CmdParser.ClearTargets.targets.forEach(s -> commandsInput.setText(s.startsWith(parts[1]) ? (parts[0] + " " + s + " ") : commandsInput.getText()));
                            break;
                        case CmdParser.EXEC:
                        case CmdParser.RESET:
                            tabComplete(parts, 1, Channels.list());
                            //Channels.list().forEach(s -> commandsInput.setText(s.startsWith(parts[1]) ? (parts[0] + " " + s + " ") : commandsInput.getText()));
                            break;
                        case CmdParser.LEAVECHANNEL:
                            tabComplete(parts, 1, Channels.list(), s -> !s.equalsIgnoreCase(Bot.getBot().getUserName()));
                            break;
                        case CmdParser.HELP:
                            tabComplete(parts, 1, CmdParser.getCommands());
                            //CmdParser.getCommands().forEach(s -> commandsInput.setText(s.startsWith(parts[1]) ? (parts[0] + " " + s + " ") : commandsInput.getText()));
                            break;
                    }
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

    private void tabComplete(String[] parts, int index, Collection<String> completions) {
        tabComplete(parts, index, completions, s -> true);
    }

    private void tabComplete(String[] parts, int index, Collection<String> completions, Predicate<String> predicate) {
        if (tabCompleteIndex != 0) {
            multipleTabComplete(parts, index);
            return;
        }

        tabCompleteList = completions.stream()
                .filter(string -> string.startsWith(parts[index]))
                .filter(predicate)
                .collect(Collectors.toList());
        if (tabCompleteList.size() == 1) {
            commandsInput.setText(getInputPartsTillIndex(parts, index) + tabCompleteList.get(0) + " ");
        } else if (tabCompleteList.size() != 0) {
            // TODO handle prompt for multiple completions
            multipleTabComplete(parts, index);
        }
    }

    private void multipleTabComplete(String[] parts, int index) {
        if (tabCompleteIndex >= tabCompleteList.size()) {
            tabCompleteIndex = 0;
        }
        commandsInput.setText(getInputPartsTillIndex(parts, index) + tabCompleteList.get(tabCompleteIndex));
        tabCompleteIndex++;
    }

    private String getInputPartsTillIndex(String[] parts, int index) {
        List<String> list = Arrays.asList(parts);
        List<String> subList = list.subList(0, ((index > list.size()) ? list.size() : index));
        String input = subList.stream().collect(Collectors.joining(" "));
        return input + ((input.length() == 0) ? "" : " ");
    }

    private void notTabCompleting() {
        tabCompleteList.clear();
        tabCompleteIndex = 0;
    }
}
