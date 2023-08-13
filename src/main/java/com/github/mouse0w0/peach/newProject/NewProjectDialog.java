package com.github.mouse0w0.peach.newProject;

import com.github.mouse0w0.peach.dialog.Alert;
import com.github.mouse0w0.peach.icon.AppIcon;
import com.github.mouse0w0.peach.javafx.layout.Container;
import com.github.mouse0w0.peach.javafx.util.FXUtils;
import com.github.mouse0w0.peach.l10n.AppL10n;
import com.github.mouse0w0.peach.project.Project;
import com.github.mouse0w0.peach.project.ProjectManager;
import com.github.mouse0w0.peach.util.FileUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;

public class NewProjectDialog extends Stage {
    private final NewProjectContext context;
    private final Container container;

    private NewProjectBuilder newProjectBuilder;

    public NewProjectDialog() {
        context = new NewProjectContext();
        context.setProjectName("untitled");

        container = new Container();

        setTitle(AppL10n.localize("dialog.newProject.title"));
        getIcons().add(AppIcon.Peach.getImage());
        setMinWidth(800);
        setMinHeight(600);
        setWidth(800);
        setHeight(600);
        initModality(Modality.APPLICATION_MODAL);

        ListView<NewProjectProvider> listView = new ListView<>(FXCollections.observableList(NewProjectProvider.EXTENSION_POINT.getExtensions()));
        listView.setCellFactory(view -> new Cell());
        MultipleSelectionModel<NewProjectProvider> selectionModel = listView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(observable -> {
            if (newProjectBuilder != null) {
                newProjectBuilder.commitData(context);
            }

            newProjectBuilder = selectionModel.getSelectedItem().createBuilder();
            container.setContent(newProjectBuilder.getNode());
            newProjectBuilder.initialize(context);
        });
        selectionModel.selectFirst();

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);

        SplitPane splitPane = new SplitPane(listView, scrollPane);

        Button finish = new Button(AppL10n.localize("button.create"));
        finish.setDefaultButton(true);
        finish.setOnAction(event -> {
            if (!newProjectBuilder.validate()) {
                return;
            }
            newProjectBuilder.commitData(context);

            Path projectDirectory = context.getProjectDirectory();
            if (projectDirectory == null) {
                Alert.error(AppL10n.localize("dialog.newProject.projectDirectoryIsNull"));
                return;
            }
            if (Files.isDirectory(projectDirectory) && FileUtils.notEmptyDirectory(projectDirectory)) {
                if (!Alert.confirm(AppL10n.localize("dialog.newProject.projectDirectoryIsNotEmpty", projectDirectory))) {
                    return;
                }
            }
            Project project = ProjectManager.getInstance().createProject(context.getProjectName(), projectDirectory);
            newProjectBuilder.setupProject(project, context);
            hide();
        });
        Button cancel = new Button(AppL10n.localize("button.cancel"));
        cancel.setCancelButton(true);
        cancel.setOnAction(event -> hide());

        HBox buttonBar = new HBox(finish, cancel);
        buttonBar.getStyleClass().add("button-bar");

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(splitPane);
        borderPane.setBottom(buttonBar);

        Scene scene = new Scene(borderPane);
        FXUtils.addStylesheet(scene, "style/style.css");
        FXUtils.addStylesheet(scene, "style/NewProjectDialog.css");
        setScene(scene);

        setOnShown(event -> Platform.runLater(() -> splitPane.setDividerPosition(0, 0.2)));
    }

    private static final class Cell extends ListCell<NewProjectProvider> {
        private final ImageView graphic = new ImageView();

        public Cell() {
            setGraphic(graphic);
        }

        @Override
        protected void updateItem(NewProjectProvider item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                graphic.setImage(null);
            } else {
                setText(item.getName());
                graphic.setImage(item.getIcon().getImage());
            }
        }
    }
}
