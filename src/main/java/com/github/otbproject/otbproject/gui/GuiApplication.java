package com.github.otbproject.otbproject.gui;

import com.github.otbproject.otbproject.fs.FSUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;

public class GuiApplication extends Application {


    private GuiController controller;
    private Tailer tailer;

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("console.fxml"));
        Parent start = loader.load();
        primaryStage.setScene(new Scene(start, 1200, 500));
        primaryStage.show();
        controller = loader.<GuiController>getController();
        File logFile = new File(FSUtil.logsDir() + File.separator + "app.log");
        tailer = Tailer.create(logFile, new CustomTailer(), 250);
        controller.logOutput.setWrapText(true);
        controller.commandsOutput.appendText(">  ");
        controller.commandsOutput.setWrapText(true);
    }

    public static void start(String[] args){
        launch(args);
    }

    class CustomTailer extends TailerListenerAdapter {
        @Override
        public void handle(String line) {
            controller.logOutput.appendText(line + "\n");
        }
    }
}
