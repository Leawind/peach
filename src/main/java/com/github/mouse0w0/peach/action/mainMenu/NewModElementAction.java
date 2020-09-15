package com.github.mouse0w0.peach.action.mainMenu;

import com.github.mouse0w0.peach.action.Action;
import com.github.mouse0w0.peach.action.ActionEvent;
import com.github.mouse0w0.peach.mcmod.ui.NewModElementUI;
import com.github.mouse0w0.peach.ui.project.ProjectWindow;
import com.github.mouse0w0.peach.ui.project.WindowManager;

public class NewModElementAction extends Action {
    @Override
    public void perform(ActionEvent event) {
        ProjectWindow window = WindowManager.getInstance().getFocusedWindow();
        NewModElementUI.show(window.getProject(), window.getStage());
    }
}
