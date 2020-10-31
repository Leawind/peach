package com.github.mouse0w0.peach.ui.icon;

import com.github.mouse0w0.peach.Peach;
import javafx.scene.image.Image;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class IconManager {

    public static IconManager getInstance() {
        return Peach.getInstance().getService(IconManager.class);
    }

    private static Map<String, Image> cacheIcons = new HashMap<>();

    public IconManager() {
        loadIcon(Icons.class, "Icons.");
    }

    private void loadIcon(Class<?> clazz, String prefix) {
        for (Field field : clazz.getFields()) {
            try {
                String iconName = prefix + field.getName();
                cacheIcons.put(iconName, (Image) field.get(null));
            } catch (IllegalAccessException ignored) {
            }
        }

        for (Class<?> inner : clazz.getClasses()) {
            loadIcon(inner, prefix + inner.getSimpleName() + ".");
        }
    }

    public Image getImage(String icon) {
        return cacheIcons.get(icon);
    }
}
