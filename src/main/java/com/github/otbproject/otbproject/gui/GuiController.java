package com.github.otbproject.otbproject.gui;

import com.github.otbproject.otbproject.cli.commands.CmdParser;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GuiController {

    @FXML
    public TextArea logOutput;
    @FXML
    public TextArea commandsOutput;
    @FXML
    public TextField commandsInput;

    @FXML
    public void command(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String input = commandsInput.getText();
            commandsOutput.appendText(input + "\n");
            commandsInput.setText("");
            commandsInput.setEditable(false);
            String output = new CmdParser().processLine(input);
            commandsOutput.appendText(output + "\n" + ">  ");
            commandsInput.setEditable(true);

        }
    }
}
