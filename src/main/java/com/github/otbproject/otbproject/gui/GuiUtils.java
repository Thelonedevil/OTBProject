package com.github.otbproject.otbproject.gui;

import javafx.application.Platform;

import java.util.Objects;

public class GuiUtils {
    public static void runSafe(final Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        }
        else {
            Platform.runLater(runnable);
        }
    }
}
