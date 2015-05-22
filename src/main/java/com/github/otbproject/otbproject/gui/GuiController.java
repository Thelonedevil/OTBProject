package com.github.otbproject.otbproject.gui;

import com.github.otbproject.otbproject.cli.commands.CmdParser;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

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

    @FXML
    public void command(KeyEvent event) {
        switch (event.getCode()) {
            case ENTER:
                String input = commandsInput.getText();
                if (input.isEmpty()) {
                    return;
                }
                cliOutput.appendText(input + "\n");
                commandsInput.clear();
                commandsInput.setEditable(false);
                commandsInput.setPromptText("Command Executing... Please Wait...");
                new Thread(() -> {
                    Thread.currentThread().setName("CLI Command Processor");
                    String output = CmdParser.processLine(input);
                    GuiUtils.runSafe(() -> {
                        cliOutput.appendText((output.isEmpty() ? "" : (output + "\n")) + ">  ");
                        commandsInput.setEditable(true);
                        commandsInput.setPromptText("Enter Command Here...");
                    });
                }).start();
                if (history.isEmpty() || !history.get(history.size() - 1).equals(input)) {
                    history.add(input);
                }
                while (history.size() > 1000) {
                    history.remove(0);
                }
                historyPointer = history.size();
                break;
            case UP:
                if (historyPointer == 0) {
                    break;
                }
                --historyPointer;
                GuiUtils.runSafe(() -> {
                    commandsInput.setText(history.get(historyPointer));
                    commandsInput.positionCaret(commandsInput.getText().length());
                });
                event.consume();
                break;
            case DOWN:
                if (historyPointer == history.size()) {
                    break;
                } else if (historyPointer == history.size() - 1) {
                    GuiUtils.runSafe(() -> {
                        commandsInput.setText("");
                    });
                    historyPointer = history.size();
                    break;
                }
                historyPointer++;
                GuiUtils.runSafe(() -> {
                    commandsInput.setText(history.get(historyPointer));
                });
                break;
        }
    }
}
