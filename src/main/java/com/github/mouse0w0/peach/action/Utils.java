package com.github.mouse0w0.peach.action;

import com.github.mouse0w0.peach.icon.Icon;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.Nullable;

class Utils {
    public static void fillMenu(ActionGroup group, ObservableList<MenuItem> items) {
        items.clear();
        for (Action action : group.getChildren()) {
            if (action instanceof ActionGroup) {
                items.add(new ActionMenu((ActionGroup) action));
            } else if (action instanceof Separator) {
                items.add(new ActionSeparator((Separator) action));
            } else {
                items.add(new ActionMenuItem(action));
            }
        }
    }

    public static void update(ActionGroup group, Object source) {
        final ActionEvent actionEvent = new ActionEvent(source);
        for (Action child : group.getChildren()) {
            child.update(actionEvent);
        }
    }

    public static void updateSeparatorVisibility(ObservableList<MenuItem> items) {
        boolean perv = false;
        MenuItem separator = null;
        for (MenuItem item : items) {
            if (item instanceof SeparatorMenuItem) {
                if (perv) {
                    perv = false;
                    separator = item;
                } else {
                    item.setVisible(false);
                }
            } else if (item.isVisible()) {
                if (separator != null) {
                    separator.setVisible(true);
                    separator = null;
                }
                perv = true;
            }
        }
        if (separator != null) {
            separator.setVisible(false);
        }
    }

    public static void setIcon(ObjectProperty<Node> graphicProperty, @Nullable Icon icon) {
        graphicProperty.set(icon != null ? new ImageView(icon.getImage()) : null);
    }

    private Utils() {
    }
}
