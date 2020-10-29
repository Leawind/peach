package com.github.mouse0w0.peach.ui.project;

import com.github.mouse0w0.peach.Peach;
import com.github.mouse0w0.peach.event.project.ProjectWindowEvent;
import com.github.mouse0w0.peach.project.Project;
import com.github.mouse0w0.peach.project.ProjectManager;
import com.github.mouse0w0.peach.ui.Icons;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public class ProjectWindow {

    private Project project;
    private Stage stage;
    private Scene scene;
    private ProjectRootPane content;

    public ProjectWindow(Project project) {
        this.project = project;
        this.stage = new Stage();
        this.content = new ProjectRootPane(project);
        this.scene = new Scene(content);
        initStage();
    }

    private void initStage() {
        stage.setScene(scene);
        stage.setTitle(project.getName());
        stage.getIcons().setAll(Icons.PEACH_16X);
        stage.setOnShown(event -> Peach.getEventBus().post(new ProjectWindowEvent.Opened(this)));
        stage.setOnHidden(event -> Peach.getEventBus().post(new ProjectWindowEvent.Closed(this)));
        stage.focusedProperty().addListener(observable -> {
            if (stage.isFocused()) {
                WindowManager.getInstance().setFocusedWindow(this);
            }
        });
        stage.setOnCloseRequest(event -> {
            ProjectManager.getInstance().closeProject(project);
            event.consume();
        });
    }

    public Project getProject() {
        return project;
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public void openTab(Tab tab) {
        addTab(tab);
        selectTab(tab);
    }

    public void addTab(Tab tab) {
        content.getTabPane().getTabs().add(tab);
    }

    public void selectTab(Tab tab) {
        content.getTabPane().getSelectionModel().select(tab);
    }

    public void removeTab(Tab tab) {
        content.getTabPane().getTabs().remove(tab);
    }

    public void requestFocus() {
        stage.requestFocus();
    }
}
