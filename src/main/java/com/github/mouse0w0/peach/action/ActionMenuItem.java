package com.github.mouse0w0.peach.action;

import com.github.mouse0w0.peach.icon.Icon;
import com.github.mouse0w0.peach.util.property.PropertyChangeListener;
import com.github.mouse0w0.peach.util.property.PropertyObservable;
import com.github.mouse0w0.peach.util.property.WeakPropertyChangeListener;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

public class ActionMenuItem extends MenuItem implements ActionHolder {
    private final Action action;
    private final PropertyChangeListener listener;

    ActionMenuItem(Action action) {
        this.action = action;

        setText(action.getText());
        Utils.setIcon(graphicProperty(), action.getIcon());
        setDisable(action.isDisable());
        setVisible(action.isVisible());

        this.listener = this::onPropertyChanged;
        action.addListener(new WeakPropertyChangeListener(listener));

        setOnAction(event -> action.perform(new ActionEvent(this)));
    }

    @Override
    public @NotNull Action getAction() {
        return action;
    }

    private void onPropertyChanged(PropertyObservable property, String propertyName, Object oldValue, Object newValue) {
        switch (propertyName) {
            case Action.TEXT_PROP -> setText((String) newValue);
            case Action.ICON_PROP -> Utils.setIcon(graphicProperty(), (Icon) newValue);
            case Action.DISABLE_PROP -> setDisable((boolean) newValue);
            case Action.VISIBLE_PROP -> setVisible((boolean) newValue);
        }
    }
}
