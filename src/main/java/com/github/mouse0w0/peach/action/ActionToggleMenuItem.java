package com.github.mouse0w0.peach.action;

import com.github.mouse0w0.peach.data.DataManager;
import com.github.mouse0w0.peach.icon.Icon;
import javafx.scene.control.CheckMenuItem;
import org.jetbrains.annotations.NotNull;

public class ActionToggleMenuItem extends CheckMenuItem implements ActionHolder, Updatable {
    private final ToggleAction action;
    private final Presentation presentation;

    ActionToggleMenuItem(ToggleAction action) {
        this.action = action;
        this.presentation = new Presentation(action, this::onPropertyChanged);

        setText(presentation.getText());
        Icon.apply(graphicProperty(), presentation.getIcon());

        selectedProperty().addListener(observable -> {
            if (isSelected()) {
                Icon.apply(graphicProperty(), null);
            } else {
                Icon.apply(graphicProperty(), presentation.getIcon());
            }
        });
        setOnAction(this::perform);
    }

    private void onPropertyChanged(String propertyName, Object oldValue, Object newValue) {
        switch (propertyName) {
            case Presentation.TEXT_PROP -> setText((String) newValue);
            case Presentation.ICON_PROP -> Icon.apply(graphicProperty(), (Icon) newValue);
            case Presentation.DISABLE_PROP -> setDisable((boolean) newValue);
            case Presentation.VISIBLE_PROP -> setVisible((boolean) newValue);
            case Presentation.SELECTED_PROP -> setSelected((boolean) newValue);
        }
    }

    @Override
    public @NotNull Action getAction() {
        return action;
    }

    @Override
    public void update() {
        action.update(new ActionEvent(null, presentation, DataManager.getInstance().getDataContext(this)));
    }

    private void perform(javafx.event.ActionEvent event) {
        action.perform(new ActionEvent(event, presentation, DataManager.getInstance().getDataContext(this)));
    }
}
