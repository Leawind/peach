package com.github.mouse0w0.peach.action;

import javafx.scene.control.SeparatorMenuItem;

public class ActionSeparator extends SeparatorMenuItem {
    public ActionSeparator(Action action) {
        getProperties().put(Action.class, action);
    }
}
