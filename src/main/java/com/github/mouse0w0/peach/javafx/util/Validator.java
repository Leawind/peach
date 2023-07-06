package com.github.mouse0w0.peach.javafx.util;

import com.github.mouse0w0.peach.javafx.control.PopupAlert;
import com.google.common.collect.ImmutableList;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public final class Validator {
    private static final PopupAlert POPUP_ALERT;

    private static final ChangeListener<Boolean> FOCUSED_LISTENER;

    private final Node node;
    private final Property<?> property;
    private final List<Check<?>> checks;

    private Check<?> invalidCheck;

    static {
        POPUP_ALERT = new PopupAlert();

        FOCUSED_LISTENER = (observable, oldValue, newValue) -> {
            ReadOnlyProperty<?> focusedProperty = (ReadOnlyProperty<?>) observable;
            Node bean = (Node) focusedProperty.getBean();

            Validator validator = getValidator(bean);
            if (validator == null) return;

            if (newValue) {
                Check<?> invalidCheck = validator.getInvalidCheck();
                if (invalidCheck != null) {
                    POPUP_ALERT.setLevel(NotificationLevel.ERROR);
                    POPUP_ALERT.setText(String.format(invalidCheck.getMessage(), validator.getProperty().getValue()));
                    POPUP_ALERT.show(bean, Side.TOP, 0, -3);
                }
            } else {
                POPUP_ALERT.hide();
                validator.validate();
            }
        };
    }

    public static <T> void register(Node node, String message, Predicate<T> predicate) {
        register(node, Check.of(message, predicate));
    }

    public static void register(Node node, Check<?>... checks) {
        Validator validator = new Validator(node, checks);
        node.getProperties().put(Validator.class, validator);
        node.focusedProperty().addListener(FOCUSED_LISTENER);
    }

    public static void unregister(Node node) {
        Validator validator = getValidator(node);
        if (validator != null) {
            node.getProperties().remove(Validator.class);
            node.focusedProperty().removeListener(FOCUSED_LISTENER);
        }
    }

    public static boolean validate(Node... nodes) {
        boolean result = true;
        Validator firstInvalid = null;
        for (Node node : nodes) {
            Validator validator = getValidator(node);
            if (validator != null && !validator.validate()) {
                result = false;
                if (firstInvalid == null) {
                    firstInvalid = validator;
                }
            }
        }

        if (firstInvalid != null) {
            Node node = firstInvalid.getNode();
            node.requestFocus();
            if (node instanceof TextInputControl) {
                ((TextInputControl) node).selectAll();
            }
        }
        return result;
    }

    public static Validator getValidator(Node node) {
        if (!node.hasProperties()) {
            return null;
        }
        return (Validator) node.getProperties().get(Validator.class);
    }

    private Validator(Node node, Check<?>... checks) {
        this.node = node;
        this.property = ValuePropertyUtils.valueProperty(node)
                .orElseThrow(() -> new IllegalArgumentException("Not found the value property of " + node.getClass()));
        Arrays.sort(checks);
        this.checks = ImmutableList.copyOf(checks);
    }

    public Node getNode() {
        return node;
    }

    public Property<?> getProperty() {
        return property;
    }

    public List<Check<?>> getChecks() {
        return checks;
    }

    public Check<?> getInvalidCheck() {
        return invalidCheck;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean validate() {
        Object value = property.getValue();
        for (Check check : getChecks()) {
            if (!check.test(value)) {
                invalidCheck = check;
                updateStyleClass(false);
                return false;
            }
        }
        invalidCheck = null;
        updateStyleClass(true);
        return true;
    }

    private void updateStyleClass(boolean valid) {
        ObservableList<String> styleClass = getNode().getStyleClass();
        styleClass.remove(Check.INVALID_STYLE_CLASS);
        if (!valid) {
            styleClass.add(Check.INVALID_STYLE_CLASS);
        }
    }
}
