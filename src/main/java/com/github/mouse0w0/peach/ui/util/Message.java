package com.github.mouse0w0.peach.ui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Message {
    public static void warning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.YES);
        alert.showAndWait();
    }

    public static void error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.YES);
        alert.showAndWait();
    }

    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
