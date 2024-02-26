package com.github.mouse0w0.peach.action;

import com.github.mouse0w0.peach.data.DataManager;
import com.github.mouse0w0.peach.icon.Icon;
import javafx.event.Event;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

public final class ActionMenu extends Menu implements ActionHolder {
    private final ActionGroup group;
    private final Presentation presentation;

    private final MenuItem placeholder = new MenuItem();

    ActionMenu(ActionGroup group) {
        this.group = group;
        this.presentation = new Presentation(group, this::onPropertyChanged);

        setText(presentation.getText());
        Utils.setIcon(graphicProperty(), presentation.getIcon());

        setOnShowing(this::updateChildren);

        // Fix JavaFX don't show empty menu.
        getItems().add(placeholder);
    }

    private void onPropertyChanged(String propertyName, Object oldValue, Object newValue) {
        switch (propertyName) {
            case Presentation.TEXT_PROP -> setText((String) newValue);
            case Presentation.ICON_PROP -> Utils.setIcon(graphicProperty(), (Icon) newValue);
            case Presentation.DISABLE_PROP -> setDisable((boolean) newValue);
            case Presentation.VISIBLE_PROP -> setVisible((boolean) newValue);
        }
    }

    @Override
    public @NotNull ActionGroup getAction() {
        return group;
    }

    @Override
    public void show() {
        fillMenu();
        super.show();
        if (getItems().isEmpty()) {
            getItems().add(placeholder);
        }
    }

    private void fillMenu() {
        getItems().clear();
        Utils.fillMenu(group, new ActionEvent(null, presentation, DataManager.getInstance().getDataContext(this)), getItems());
    }

    private void updateChildren(Event event) {
        Utils.update(getItems());
        Utils.updateSeparatorVisibility(getItems());
    }

    void update() {
        group.update(new ActionEvent(null, presentation, DataManager.getInstance().getDataContext(this)));
    }
}
