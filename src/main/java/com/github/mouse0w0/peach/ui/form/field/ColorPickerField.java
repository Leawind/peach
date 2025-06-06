package com.github.mouse0w0.peach.ui.form.field;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class ColorPickerField extends ValueField<Color> {
    private final ObjectProperty<Color> value = new SimpleObjectProperty<>(this, "value");

    @Override
    public final ObjectProperty<Color> valueProperty() {
        return value;
    }

    @Override
    public final Color getValue() {
        return value.get();
    }

    @Override
    public final void setValue(Color value) {
        this.value.set(value);
    }

    @Override
    protected Node createEditorNode() {
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setMaxWidth(Double.MAX_VALUE);
        colorPicker.valueProperty().bindBidirectional(valueProperty());
        colorPicker.disableProperty().bind(disableProperty());
        return colorPicker;
    }
}
