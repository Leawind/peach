package com.github.mouse0w0.peach.action.mainMenu;

import com.github.mouse0w0.peach.action.Action;
import com.github.mouse0w0.peach.action.ActionEvent;

import java.awt.*;
import java.net.URI;

public class DonateAction extends Action {
    @Override
    public void perform(ActionEvent event) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI("https://afdian.net/@mouse"));
            }
        } catch (Exception ignored) {
        }
    }
}
